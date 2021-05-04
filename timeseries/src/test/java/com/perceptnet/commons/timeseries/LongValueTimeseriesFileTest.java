package com.perceptnet.commons.timeseries;

import com.perceptnet.commons.utils.Pair;
import com.perceptnet.commons.utils.RandomUtils;
import com.perceptnet.commons.utils.RegexUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 21.08.2018
 */
public class LongValueTimeseriesFileTest {
    private File file;

    private long[] times = new long[1000];
    private long[] values = new long[1000];

    private long[] rTimes = new long[times.length];
    private long[] rValues = new long[values.length];

    @BeforeClass(groups = {UNIT})
    @AfterClass(groups = {UNIT})
    public void deleteTestFiles() {
        File[] files = new File(".").listFiles();
        for (File f : files) {
            if (f.isFile() && f.getName().startsWith("test") && f.getName().contains(".tsdata")) {
                f.delete();
            }
        }
    }

    @BeforeMethod(groups = {UNIT})
    public void beforeMethod(Method method) {
        file = new File(method.getName() + ".tsdata");
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    @Test(groups = {UNIT})
    public void testAppend() throws Exception {
        LongValueTimeseriesFile f = new LongValueTimeseriesFile(file, "unittest data");
        long startTs = System.currentTimeMillis() - 100000000L;
        f.append(startTs, 1L);
        f.append(startTs + 60000, 22L );
        f.append(startTs + 120000, 333L );
        f.append(startTs + 180000, 444L );

        int read = f.iterator().read(times, values);
        assertEquals(read, 4);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (int i = 0; i < read; i++) {
            System.out.println(sdf.format(new Date(times[i])) +"  --  " + values[i]);
        }

        assertEquals(times[0], startTs);
        assertEquals(values[0], 1L);

        assertEquals(times[1], startTs + 60000);
        assertEquals(values[1], 22L);

        assertEquals(times[2], startTs + 120000);
        assertEquals(values[2], 333L);

        assertEquals(times[3], startTs + 180000);
        assertEquals(values[3], 444L);

        f.close();
    }

    @Test(groups = {UNIT})
    private void testHalfRangedIterator() throws Exception {
        LongValueTimeseriesFile f = new LongValueTimeseriesFile(file, "unittest data");
        appendRandomValues(f, times.length);

        //Between 50th and 51st time:
        long timeA = times[50];
        long timeB = times[51];
        LongValueTimeseriesIterator iter = f.iterator(timeA + (timeB - timeA) / 2);
        int read = iter.read(rTimes, rValues);
        assertEquals(read, times.length - 51);

        for (int i = 0; i < read; i++) {
            assertEquals(rTimes[i], times[51 + i], "Wrong time at " + i);
            assertEquals(rValues[i], values[51 + i], "Wrong value at " + i);
        }

        //Staring exactly from times[649]:
        iter = f.iterator(times[649]);
        read = iter.read(rTimes, rValues);
        assertEquals(read, times.length - 649);

        for (int i = 0; i < read; i++) {
            assertEquals(rTimes[i], times[649 + i], "Wrong time at " + i);
            assertEquals(rValues[i], values[649 + i], "Wrong value at " + i);
        }

        //Between 998th and 999th time:
        timeA = times[998];
        timeB = times[999];
        iter = f.iterator(timeA + (timeB - timeA) / 2);
        read = iter.read(rTimes, rValues);
        assertEquals(read, 1);

        assertEquals(rTimes[0], times[999], "Wrong time");
        assertEquals(rValues[0], values[999], "Wrong value");
    }

    @Test(groups = {UNIT})
    private void testHalfRangedIteratorPrecedingFirstTime() throws Exception {
        LongValueTimeseriesFile f = new LongValueTimeseriesFile(file, "unittest data");
        appendRandomValues(f, times.length);

        //Preceding first time - read all
        LongValueTimeseriesIterator iter = f.iterator(times[0] - 1);
        int read = iter.read(rTimes, rValues);
        assertEquals(read, times.length);

        for (int i = 0; i < read; i++) {
            assertEquals(rTimes[i], times[i], "Wrong time at " + i);
            assertEquals(rValues[i], values[i], "Wrong value at " + i);
        }

        //Exactly first time - read all
        iter = f.iterator(times[0]);
        read = iter.read(rTimes, rValues);
        assertEquals(read, times.length);

        for (int i = 0; i < read; i++) {
            assertEquals(rTimes[i], times[i], "Wrong time at " + i);
            assertEquals(rValues[i], values[i], "Wrong value at " + i);
        }
    }


    @Test(groups = {UNIT})
    private void testHalfRangedIteratorExactLastTsStartTime() throws Exception {
        LongValueTimeseriesFile f = new LongValueTimeseriesFile(file, "unittest data");
        appendRandomValues(f, times.length);

        //Preceding first time - read all
        LongValueTimeseriesIterator iter = f.iterator(times[times.length - 1]);
        int read = iter.read(rTimes, rValues);
        assertEquals(read, 1);

        assertEquals(rTimes[0], times[999], "Wrong time");
        assertEquals(rValues[0], values[999], "Wrong value");
    }

    @Test(groups = {UNIT})
    private void testHalfRangedIteratorJustBeforeLastTsStartTime() throws Exception {
        LongValueTimeseriesFile f = new LongValueTimeseriesFile(file, "unittest data");
        appendRandomValues(f, times.length);

        //Preceding first time - read all
        LongValueTimeseriesIterator iter = f.iterator(times[times.length - 1] - 1);
        int read = iter.read(rTimes, rValues);
        assertEquals(read, 1);

        assertEquals(rTimes[0], times[999], "Wrong time");
        assertEquals(rValues[0], values[999], "Wrong value");
    }

    @Test(groups = {UNIT})
    private void testHalfRangedIteratorAfterLastTsStartTime() throws Exception {
        LongValueTimeseriesFile f = new LongValueTimeseriesFile(file, "unittest data");
        appendRandomValues(f, times.length);

        //Preceding first time - read all
        LongValueTimeseriesIterator iter = f.iterator(times[times.length - 1] + 1000);
        int read = iter.read(rTimes, rValues);
        assertEquals(read, 0);
    }

    @Test(groups = {UNIT})
    public void testConcurrentReads() throws Exception {
        final LongValueTimeseriesFile f = new LongValueTimeseriesFile(file, "unittest data");
        appendRandomValues(f, times.length);

        final AtomicInteger nR = new AtomicInteger(0);
        final Set<String> firedReaders = Collections.synchronizedSet(new HashSet<String>());

        //Test reader:
        final Runnable rR = new Runnable() {
            @Override
            public void run() {
                while (nR.get() < 1000) {
                    long start = times[0];
                    long nL = times[times.length - 1];
                    long end = start + RandomUtils.nextLong(0, nL);
                    LongValueTimeseriesIterator it = f.iterator(start, end);
                    long[] tsB = new long[10];
                    long[] valB = new long[10];
                    int read = it.read(tsB, valB);
                    if (read > 0) {
                        nR.incrementAndGet();
                        firedReaders.add(Thread.currentThread().getName());
                    }
                }
            }
        };

        Map<Integer, Thread> readers = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(rR, "r" + i);
            readers.put(i, t);
        }

        for (Thread t : readers.values()) {
            t.start();
        }

        Thread.sleep(1000L);

        System.out.println("Fired readers: " + firedReaders.size());
        assertEquals(firedReaders.size(), readers.size(), "Not all reading threads have red values");
    }

    @Test(groups = {UNIT}, enabled = false)
    public void testConcurrentWritesAndReads() throws Exception {
        final LongValueTimeseriesFile f = new LongValueTimeseriesFile(file, "unittest data");
        final AtomicInteger nW = new AtomicInteger(0);
        final AtomicInteger nR = new AtomicInteger(0);
        final AtomicLong curTs = new AtomicLong(0L);
        final Set<String> firedWriters = Collections.synchronizedSet(new HashSet<String>());
        final Set<String> firedReaders = Collections.synchronizedSet(new HashSet<String>());

        //Test writer:
        final Runnable rW = new Runnable() {
            @Override
            public void run() {
                while (nW.get() < 1000) {
                    long v = RandomUtils.nextLong(0, 1000);
                    try {
                        f.append(curTs.getAndIncrement(), v);
                        int nn = nW.incrementAndGet();
                        firedWriters.add(Thread.currentThread().getName());
                    } catch (IOException e) {
                        throw new RuntimeException("" + e, e);
                    } catch (IllegalArgumentException e) {
                        if (!e.getMessage().equals("Time sequence must be increasing")) {
                            throw e;
                        }
                    }
                }
            }
        };

        //Test reader:
        final Runnable rR = new Runnable() {
            @Override
            public void run() {
                while (nR.get() < 1000) {
                    long curLastTs = curTs.get();
                    long start = RandomUtils.nextLong(0L, curLastTs);
                    long nL = curLastTs - start;
                    long end = start + RandomUtils.nextLong(0, nL);
                    LongValueTimeseriesIterator it = f.iterator(start, end);
                    long[] tsB = new long[10];
                    long[] valB = new long[10];
                    int read = it.read(tsB, valB);
                    if (read > 0) {
                        nR.incrementAndGet();
                        firedReaders.add(Thread.currentThread().getName());
                    }
                }
            }
        };

        Map<Integer, Thread> writers = new HashMap<>();
        Map<Integer, Thread> readers = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(rW, "w" + i);
            writers.put(i, t);
        }

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(rR, "r" + i);
            readers.put(i, t);
        }

        List<Thread> allThreads = new ArrayList<>(writers.size() + readers.size());
        allThreads.addAll(writers.values());
        allThreads.addAll(readers.values());
        Collections.shuffle(allThreads);
        for (Thread t : allThreads) {
            t.start();
        }

        outer:
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000L);
            for (Thread t : allThreads) {
                if (t.isAlive()) {
                    continue outer;
                }
            }
            break ;
        }

        System.out.println("Fired writers: " + firedWriters.size());
        System.out.println("Fired readers: " + firedReaders.size());
        assertEquals(firedWriters.size(), writers.size(), "Not all writing threads have appended values");
        assertEquals(firedReaders.size(), readers.size(), "Not all reading threads have red values");
    }

    private void appendRandomValues(LongValueTimeseriesFile f, int num) {
        if (num > times.length) {
            throw new IllegalArgumentException("Too big num: " + num);
        }
        long startTs = System.currentTimeMillis() - 100000000L;
        for (int i = 0; i < num; i++) {
            times[i] = i == 0 ? startTs : times[i - 1] + RandomUtils.nextLong(60000, 600000);
            values[i] = RandomUtils.nextLong(0, 10000);
        }
        f.append(times, values, 0, num);
    }

}