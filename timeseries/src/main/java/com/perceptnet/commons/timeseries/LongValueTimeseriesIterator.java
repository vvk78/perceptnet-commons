package com.perceptnet.commons.timeseries;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import static com.perceptnet.commons.timeseries.TimeseriesUtils.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 21.08.2018
 */
public class LongValueTimeseriesIterator {
    private final LongValueTimeseriesFile f;
    private final RandomAccessFile raf;
    private final FileChannel channel;
    private final int entrySize;
    private final Long fromTs;
    private final Long toTs;


    private final ByteBuffer buff;
    private final ByteBuffer entryBuff;
    private byte[] auxBuffer;
    private int curBuffActualSize;

    private long nextFileOffset;

    private boolean exhausted;

    LongValueTimeseriesIterator(LongValueTimeseriesFile f) {
        this(f, null, null);
    }

    LongValueTimeseriesIterator(LongValueTimeseriesFile f, Long fromTs, Long toTs) {
        this.f = f;
        this.raf = f.getRaf();
        this.channel = f.getChannel();
        this.entrySize = f.getHeader().getItemDataBlockSize();
        int blocks = 1024 / entrySize;
        if (blocks <= 0) {
            throw new IllegalStateException("Too big blocks");
        }
        this.buff = ByteBuffer.allocate(blocks * entrySize);
        this.entryBuff = ByteBuffer.allocate(entrySize);
        this.curBuffActualSize = 0;

        this.fromTs = fromTs;
        this.toTs = toTs;

        this.nextFileOffset = -1;
    }

    /**
     * Reads required amount of entries (provided there is enough in the file) into provided buffers
     *
     * @param times  times buffer
     * @param values values buffer
     * @param offset offset to fill buffers starting from
     * @param num    desired number of entries to read (is not guaranteed to be read, if there is no data available)
     * @return actual number of bytes read in the buffer
     */
    public int read(long[] times, long[] values, int offset, int num) {
        if (exhausted) {
            return 0;
        }
        assertTimeseriesArraysOffsetBounds(times.length, values.length, offset, num);
        int count = 0;
        for (int i = offset; i < num; i++) {
            if (feedNextEntry()) {
                long ts = entryBuff.getLong();
                if (toTs != null && ts > toTs) {
                    exhausted = true;
                    break;
                }
                times[i] = ts;
                values[i] = entryBuff.getLong();
                count++;
            }
        }
        return count;
    }

    private boolean feedNextEntry() {
        return doFeedNextEntry(true);
    }

    private boolean doFeedNextEntry(boolean mayFeedBuffer) {
        entryBuff.position(0);
        if (buff.position() + entrySize < curBuffActualSize) {
            if (!mayFeedBuffer || !feedNextBuffer()) {
                return false;
            }
            return doFeedNextEntry(false);
        }
        buff.get(entryBuff.array());
        entryBuff.position(0);
        return true;
    }

    private boolean feedNextBuffer() {
        try (FileLock lock = channel.lock(nextFileOffset, nextFileOffset + buff.capacity(), false)) {
            channel.position(nextFileOffset);
            buff.position(0);
            int left = relocateCurrBufferRemainsIfNeeded();
            curBuffActualSize = channel.read(buff, left) + left;
            buff.position(0);
            return curBuffActualSize > 0;
        } catch (IOException e) {
            throw new RuntimeException("" + e, e);
        }
    }

    /**
     * Relocates not used remains of current buffer (if needed). (Basically this should never happen, as buffer is allocated to lodge even number of
     * entries)
     *
     * @return start offset to fill the buffer with new portion of bytes.
     */
    private int relocateCurrBufferRemainsIfNeeded() {
        int left = curBuffActualSize - buff.position();
        if (left > 0) {
            if (auxBuffer == null || auxBuffer.length < left) {
                auxBuffer = new byte[left];
            }
            System.arraycopy(buff.array(), buff.position(), auxBuffer, 0, left);
            buff.position(0);
            System.arraycopy(auxBuffer, 0, buff.array(), 0, left);
        }
        return left;
    }
}
