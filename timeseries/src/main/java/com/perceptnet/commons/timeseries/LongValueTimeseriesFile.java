package com.perceptnet.commons.timeseries;

import com.perceptnet.commons.utils.IoUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.locks.ReentrantLock;

import static com.perceptnet.commons.timeseries.TimeseriesUtils.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 15.08.2018
 */
public class LongValueTimeseriesFile implements Closeable {
    public static final int HEADER_SIZE = 512;
    public static final int INDEX_SIZE = 1024;

    public static final int HEAD_SIZE = HEADER_SIZE; // + INDEX_SIZE

    private HeaderInfo header;
    private int entrySize;
    private RandomAccessFile raf;

    private boolean lastTsKnown;
    private long lastTs;

    private long[] oneTsBuff = new long[1];
    private long[] oneValBuff = new long[1];

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * This iterator is used internally for probing file for values at different positions
     */
    private LongValueTimeseriesIterator probeIterator;

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
            this.raf.readFully(headerBuff);
            this.header = HeaderInfo.readHeaderInfoFromBytes(headerBuff);
            this.entrySize = this.header.getItemDataBlockSize();
            this.probeIterator = new LongValueTimeseriesIterator(this, null, HEAD_SIZE, null, null, entrySize);
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
            this.raf = new RandomAccessFile(f, "rw");
            this.entrySize = 16;
            this.header = new HeaderInfo(this.entrySize, label, null);
            byte[] b = header.writeToBytes(new byte[HEAD_SIZE]);
            this.raf.write(b);
            this.probeIterator = new LongValueTimeseriesIterator(this, null, HEAD_SIZE, null, null, entrySize);
        } catch (RuntimeException e) {
            IoUtils.closeSafely(raf);
            throw e;
        } catch (Exception e) {
            IoUtils.closeSafely(raf);
            throw new RuntimeException("Cannot open file " + f + " due to: " + e, e);
        }
    }

    public LongValueTimeseriesIterator iterator() {
        return new LongValueTimeseriesIterator(this);
    }

    public LongValueTimeseriesIterator iterator(long fromTs) {
        return iterator(fromTs, null);
    }

    public LongValueTimeseriesIterator iterator(long fromTs, Long toTs) {
        PositionInfo pi = findPrecedingPositionToStartIterator(fromTs);
        if (pi == null) {
            return LongValueTimeseriesIterator.exhausted();
        }
        if (pi.precedingTsPosition != -1) {
            assertPositionAligned(pi.precedingTsPosition);
            if (pi.precedingTs == fromTs) {
                return new LongValueTimeseriesIterator(this, pi, pi.precedingTsPosition, fromTs, toTs);
            } else {
                return new LongValueTimeseriesIterator(this, pi, pi.precedingTsPosition + entrySize, fromTs, toTs);
            }
        } else {
            return new LongValueTimeseriesIterator(this, null, HEAD_SIZE, fromTs, toTs);
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
        lock();
        try {
            assertTimeseriesArraysOffsetBounds(times.length, values.length, offset, num);
            assertSequence(!isEmpty() ? lastKnownTs() : Long.MIN_VALUE, times, offset, num);
            long initialSize;
            try {
                initialSize = raf.length();
                raf.seek(initialSize);
            } catch (IOException e) {
                throw new RuntimeException("" + e, e);
            }
            try {
                try (FileLock lock = raf.getChannel().lock(initialSize, num * 16, false)) {
                    long ts = 0L;
                    ByteBuffer b = ByteBuffer.allocate(num * 16);
                    for (int i = offset; i < num; i++) {
                        int idx = offset + i;
                        ts = times[idx];
                        //raf.writeLong(ts);
                        //raf.writeLong(v);
                        b.putLong(ts);
                        b.putLong(values[idx]);
                    }
                    b.position(0);
                    raf.getChannel().write(b);

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
                throw new RuntimeException("Cannot extend timeseries: " + e, e);
            }
        } finally {
            unlock();
        }
    }

    public boolean isEmpty() {
        lock();
        try {
            return raf.length() == HEADER_SIZE;
        } catch (IOException e) {
            throw new RuntimeException("" + e, e);
        } finally {
            unlock();
        }
    }

    private long lastKnownTs() {
        if (lastTsKnown) {
            return lastTs;
        }
        lock();
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
        } finally {
            unlock();
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

    /**
     * Finds position for the first entry with ts preceding or equal given ts
     *
     * @param ts
     * @return
     */
    PositionInfo findPrecedingPositionToStartIterator(long ts) {
        lock();
        try {
            long length = raf.length();
            long pl = length - HEAD_SIZE;
            long entries = pl / entrySize;
            if (pl % entrySize != 0) {
                throw new RuntimeException("File is corrupt");
            }
            if (entries == 0) {
                return null;
            }
            long firstTs = readTs(HEAD_SIZE);
            long firstVal = oneValBuff[0];
            if (firstTs == ts || (firstTs < ts && entries == 1)) {
                return new PositionInfo(oneTsBuff[0], oneValBuff[0], new Long(HEAD_SIZE));
            }
            if (firstTs > ts) {
                return new PositionInfo(oneTsBuff[0], oneValBuff[0], -1L);
            } else {
                long lastTs = readTs(length - entrySize);
                long lastVal = oneValBuff[0];
                if (lastTs == ts) {
                    return new PositionInfo(oneTsBuff[0], oneValBuff[0], length - entrySize);
                } else if (lastTs < ts) {
                    return null;
                }
                return binarySearchPrecedingPosition(ts, HEAD_SIZE, firstTs, firstVal, length - entrySize, lastTs, lastVal);
            }
        } catch (IOException e) {
            throw new RuntimeException("" + e, e);
        } finally {
            unlock();
        }
    }

    void lock() {
        lock.lock();
    }

    void unlock() {
        lock.unlock();
    }

    PositionInfo binarySearchPrecedingPosition(long ts, long fromPosition, long fromTs, long fromVal, long toPosition, long toTs, long toVal) throws IOException {
        if (toPosition == fromPosition) {
            if (fromTs <= ts) {
                return new PositionInfo(fromTs, fromVal, fromPosition);
            }
            return null; //new PositionInfo(oneTsBuff[0], oneValBuff[0], fromPosition);
        }
        long ed = (toPosition - fromPosition) / entrySize;
        if (ed == 0) {
            throw new IllegalStateException("Illegal position");
        } else if (ed == 1) {
            if (fromTs > ts) {
                return null;
            }
            if (fromTs <= ts) {
                return new PositionInfo(fromTs, fromVal, fromPosition);
            } else if (toPosition <= ts) {
                throw new IllegalStateException(); //basically this should never happen
                //return new PositionInfo(toTs, toVal, toPosition);
            } else {
                throw new IllegalStateException(); //basically this should never happen
                //return null;
            }
        } else {
            long halfEd = ed / 2;
            long k = fromPosition + (halfEd * entrySize);
            assertPositionAligned(k);
            long kTs = readTs(k);
            long kVal = oneValBuff[0];
            if (kTs == ts) {
                return new PositionInfo(kTs, kVal, k);
            } else if (kTs < ts) {
                return binarySearchPrecedingPosition(ts, k, kTs, kVal, toPosition, toTs, toVal);
            } else {
                return binarySearchPrecedingPosition(ts, fromPosition, fromTs, fromVal, k, kTs, kVal);
            }
        }
    }

    private boolean probe(long position) {
        probeIterator.move(position);
        return probeIterator.read(oneTsBuff, oneValBuff) > 0;
    }

    /**
     * Reads timestamp at given position or throws IllegalStateException if it is not possible.
     * Note that returned value is the same as oneTsBuff[0].
     */
    private long readTs(long position) {
        if (!probe(position)) {
            throw new IllegalStateException("Cannot probe at " + position);
        }
        return oneTsBuff[0];
    }

    private void assertPositionAligned(long position) {
        assert (position - HEAD_SIZE) % entrySize == 0 : "not aligned offset: " + position;
    }


}
