/*
 * Copyright 2017 Perceptnet
 *
 * This source code is Perceptnet Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 *
 */
package com.perceptnet.commons.utils;

import java.util.Comparator;

/**
 * Created by vkorovkin on 07.08.2015.
 */
public class TupleComparator implements Comparator<Tuple<Comparable>> {
    private final boolean startFromLowerIndex;
    private final boolean nullsGreater;
    private final int comparableSize;

    public TupleComparator(boolean startFromLowerIndex, boolean nullsGreater, int comparableSize) {
        this.startFromLowerIndex = startFromLowerIndex;
        this.nullsGreater = nullsGreater;
        this.comparableSize = comparableSize;
    }

    @Override
    public int compare(Tuple<Comparable> t1, Tuple<Comparable> t2) {
        assertTupleSizeIsValid(t1);
        assertTupleSizeIsValid(t2);
        if (startFromLowerIndex) {
            for (int i = 0; i < comparableSize; i++) {
                int result = doCompareItems(t1.get(i), t2.get(i));
                if (result != 0) {
                    return result;
                }
            }
        } else {
            for (int i = comparableSize - 1; i >= 0; i--) {
                int result = doCompareItems(t1.get(i), t2.get(i));
                if (result != 0) {
                    return result;
                }
            }
        }
        return 0;
    }

    private void assertTupleSizeIsValid(Tuple<Comparable> t) {
        if (t.size() < comparableSize) {
            throw new IllegalStateException("Tuple " +
                    t + " has size " + t.size() + ", which is less than comparable size " + comparableSize);
        }
    }

    private int doCompareItems(Comparable ti1, Comparable ti2) {
        return ComparableUtils.compareNullable(ti1, ti2, nullsGreater);
    }
}
