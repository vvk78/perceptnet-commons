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
    private PositionInfo precedingPi;

    private static final LongValueTimeseriesIterator EXHAUSTED = new LongValueTimeseriesIterator();

    static LongValueTimeseriesIterator exhausted() {
        return EXHAUSTED;
    }

    /**
     * Exhausted iterator, will never return anything
     */
    private LongValueTimeseriesIterator() {
        this.exhausted = true;
        this.buff = null;
        this.entryBuff = null;
        this.f = null;
        this.raf = null;
        this.channel = null;
        this.fromTs = null;
        this.toTs = null;
        this.entrySize = 0;
    }

    LongValueTimeseriesIterator(LongValueTimeseriesFile f) {
        this(f, null, LongValueTimeseriesFile.HEAD_SIZE, null, null);
    }

    LongValueTimeseriesIterator(LongValueTimeseriesFile f, PositionInfo precedingPi, long nextFileOffset, Long fromTs, Long toTs) {
        this(f, precedingPi, nextFileOffset, fromTs, toTs, 2048);
    }

    LongValueTimeseriesIterator(LongValueTimeseriesFile f, PositionInfo precedingPi, long nextFileOffset, Long fromTs, Long toTs, int internalBufferSize) {
        this.f = f;
        this.raf = f.getRaf();
        this.channel = f.getChannel();
        this.entrySize = f.getHeader().getItemDataBlockSize();
        this.precedingPi = precedingPi;
        this.nextFileOffset = nextFileOffset;
        int entriesInBuff = internalBufferSize / entrySize;
        if (entriesInBuff <= 0) {
            throw new IllegalStateException("Too big entries for this size of internal buffer (" + internalBufferSize + ")");
        }
        this.buff = ByteBuffer.allocate(entriesInBuff * entrySize);
        this.entryBuff = ByteBuffer.allocate(entrySize);
        this.curBuffActualSize = 0;

        this.fromTs = fromTs;
        this.toTs = toTs;
    }

    public int read(long[] times, long[] values) {
        return read(times, values, 0, Math.min(times.length, values.length));
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
            } else {
                break;
            }
        }
        return count;
    }

    private boolean feedNextEntry() {
        if (buff.position() + entrySize > curBuffActualSize) {
            if (!feedNextBuffer()) {
                return false;
            }
            if (buff.position() + entrySize > curBuffActualSize) {
                return false;
            }
        }
        buff.get(entryBuff.array());
        entryBuff.position(0);
        return true;
    }

    private boolean feedNextBuffer() {
        f.lock();
        try (FileLock lock = channel.lock(nextFileOffset, nextFileOffset + buff.capacity(), true)) {
            raf.seek(nextFileOffset);
            int left = relocateCurrBufferRemainsIfNeeded();
            byte[] ba = buff.array();
            curBuffActualSize = raf.read(ba, left, ba.length - left) + left;
            if (curBuffActualSize <= 0) {
                return false;
            }
            buff.position(0);
            nextFileOffset = nextFileOffset + curBuffActualSize;
            return curBuffActualSize > 0;
        } catch (IOException e) {
            throw new RuntimeException("" + e, e);
        } finally {
            f.unlock();
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

    /**
     * Basically this method is used for internal needs. It prepares iterator for reading starting at new file offset
     *
     * @param nextFileOffset
     */
    void move(long nextFileOffset) {
        this.buff.position(0);
        this.entryBuff.position(0);
        this.nextFileOffset = nextFileOffset;
        this.curBuffActualSize = 0;
        this.exhausted = false;
    }


}
