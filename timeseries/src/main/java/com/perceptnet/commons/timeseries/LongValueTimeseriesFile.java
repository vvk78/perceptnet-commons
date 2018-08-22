package com.perceptnet.commons.timeseries;

import com.perceptnet.commons.utils.IoUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import static com.perceptnet.commons.timeseries.TimeseriesUtils.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 15.08.2018
 */
public class LongValueTimeseriesFile implements Closeable {
    public static final int HEADER_SIZE = 512;
    public static final int INDEX_SIZE = 1024;

    public static final int HEAD_SIZE = HEADER_SIZE; // + INDEX_SIZE

    private HeaderInfo header;
    private RandomAccessFile raf;

    private boolean lastTsKnown;
    private long lastTs;

    private long[] oneTsBuff = new long[1];
    private long[] oneValBuff = new long[1];

    public LongValueTimeseriesFile(File f) {
        try {
            if (f.exists() && f.isFile()) {
                raf = new RandomAccessFile(f, "rw");
            } else {
                throw new RuntimeException("File does not exist or is a directory");
            }

            if (raf.length() < HEAD_SIZE) {
                throw new RuntimeException("File " + f + " is corrupt");
            }
            byte[] headerBuff = new byte[HEADER_SIZE];
            raf.readFully(headerBuff);
            header = HeaderInfo.readHeaderInfoFromBytes(headerBuff);
        } catch (Exception e) {
            IoUtils.closeSafely(raf);
            throw new RuntimeException("Cannot open file " + f + " due to: " + e, e);
        }
    }

    public LongValueTimeseriesFile(File f, String label) {
        try {
            if (f.exists() && f.isFile()) {
                throw new RuntimeException("File " + f + " already exists");
            } else if (f.exists() && f.isDirectory()) {
                throw new RuntimeException("" + f + " is an existing directory");
            }

            if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            raf = new RandomAccessFile(f, "rw");
            header = new HeaderInfo(16, label, null);
            byte[] b = header.writeToBytes(new byte[HEAD_SIZE]);
            raf.write(b);
        } catch (RuntimeException e) {
            IoUtils.closeSafely(raf);
            throw e;
        } catch (Exception e) {
            IoUtils.closeSafely(raf);
            throw new RuntimeException("Cannot open file " + f + " due to: " + e, e);
        }
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }

    public boolean isOpen() {
        return raf.getChannel().isOpen();
    }

    public void append(long time, long value) throws IOException {
        oneTsBuff[0] = time;
        oneValBuff[0] = value;
        append(oneTsBuff, oneValBuff, 0, 1);
    }

    public void append(long[] times, long[] values, int offset, int num) {
        assertTimeseriesArraysOffsetBounds(times.length, values.length, offset, num);
        assertSequence(!isEmpty() ? lastKnownTs() : Long.MIN_VALUE, times, offset, num);
        long initialSize;
        try {
            initialSize = raf.length();
        } catch (IOException e) {
            throw new RuntimeException("" + e, e);
        }
        try {
            try (FileLock lock = raf.getChannel().lock(initialSize, num * 16, true)) {
                long ts = 0L;
                for (int i = 0; i <= num; i++) {
                    int idx = offset + i;
                    ts = times[idx];
                    long v = values[idx];
                    raf.writeLong(ts);
                    raf.writeLong(v);
                }
                if (num > 0) {
                    updateLastTs(ts);
                }
            }

        } catch (IOException e) {
            try {
                raf.setLength(initialSize);
            } catch (IOException e2) {
                System.err.println("IOException while rollback: " + e2 + " file may be in inconsistent state");
            }
            lastTsKnown = false;
        }
    }

    public boolean isEmpty() {
        try {
            return raf.length() == HEADER_SIZE;
        } catch (IOException e) {
            throw new RuntimeException("" + e, e);
        }
    }

    private long lastKnownTs() {
        if (lastTsKnown) {
            return lastTs;
        }
        try {
            int blockSize = header.getItemDataBlockSize();
            long l = raf.length();
            long bl = l - HEAD_SIZE;
            assert bl % blockSize == 0 : "corrupt timeseries file";
            raf.seek(l - blockSize);
            lastTs = raf.readLong();
            lastTsKnown = true;
            return lastTs;
        } catch (IOException e) {
            throw new RuntimeException("" + e, e);
        }
    }

    private void updateLastTs(long ts) {
        lastTs = ts;
        lastTsKnown = true;
    }

    public HeaderInfo getHeader() {
        return header;
    }

    public RandomAccessFile getRaf() {
        return raf;
    }

    FileChannel getChannel() {
        return raf.getChannel();
    }


}
