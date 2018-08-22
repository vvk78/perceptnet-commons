package com.perceptnet.commons.timeseries;

import java.util.Arrays;
import static com.perceptnet.commons.timeseries.TimeseriesUtils.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 16.08.2018
 */
public class IndexInfo {
    private int actualEntriesNum;
    private long[] times;
    private long[] offsets;

    public IndexInfo(int maxIndexSize, int actualEntriesNum) {
        if (actualEntriesNum < 0) {
            throw new IllegalArgumentException("actualEntriesNum < 0");
        }
        if (actualEntriesNum > maxIndexSize) {
            throw new IllegalArgumentException("actualEntriesNum > maxIndexSize (" + actualEntriesNum + " > " + maxIndexSize + ")");
        }
        this.actualEntriesNum = actualEntriesNum;
        this.times = new long[maxIndexSize];
        this.offsets = new long[maxIndexSize];
    }

    public void put(long time, long offset) {
        if (actualEntriesNum == 0) {
            times[0] = time;
            offsets[0] = offset;
            actualEntriesNum++;
            return;
        }
        int itemIndex = Arrays.binarySearch(times, 0, actualEntriesNum, time);
        if (itemIndex < 0) {
            int i = -itemIndex - 1;
            if (i > 0) {
                assertTimeSequence(times[i-1], time);
            }
            if (i < actualEntriesNum) {
                if (i < actualEntriesNum - 1) {
                    assertTimeSequence(time, times[i + 1]);
                }
                times[i] = time;
            }
            //todo not completed
        }
    }


}
