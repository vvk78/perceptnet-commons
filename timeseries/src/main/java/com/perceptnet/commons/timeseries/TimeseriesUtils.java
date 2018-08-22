package com.perceptnet.commons.timeseries;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 20.08.2018
 */
public class TimeseriesUtils {
    static void assertTimeSequence(long tsA, long tsB) {
        if (tsA >= tsB) {
            throw new IllegalArgumentException("Time sequence must be increasing");
        }
    }

    static void assertTimeseriesArraysOffsetBounds(int timesSize, int valuesSize, int offset, int num) {
        int endIdx = offset + num;
        if (offset < 0 || num < 0 || endIdx > timesSize || endIdx > valuesSize) {
            throw new IllegalArgumentException("Times size, values size, offset or num is out of allowed range (" + timesSize + ", " +
                                                                                                            valuesSize +  ", " + offset +  ", " + num + ")");
        }
    }

    static void assertSequence(long prevTs, long[] times, int offset, int num) {
        for (int i = offset; i < num; i++) {
            long ts = times[i];
            if (prevTs != Long.MIN_VALUE) {
                assertTimeSequence(prevTs, ts);
            }
            prevTs = ts;
        }
    }
}
