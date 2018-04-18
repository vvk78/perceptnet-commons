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

import com.perceptnet.abstractions.Range;

import java.util.Comparator;

/**
 * Created by vkorovkin on 17.03.15.
 */
public class AdaptedRangeComparator<T> implements Comparator<T> {
    private final RangeAdaptor<T> rangeAdaptor;
    private final RangeComparator rangeComparator;

    public AdaptedRangeComparator(RangeAdaptor<T> rangeAdaptor) {
        if (rangeAdaptor == null) {
            throw new NullPointerException("Range adaptor is not defined");
        }
        this.rangeAdaptor = rangeAdaptor;
        this.rangeComparator = new RangeComparator();
    }

    @Override
    public int compare(T o1, T o2) {
        Range r1 = rangeAdaptor.adapt(o1);
        Range r2 = rangeAdaptor.adapt(o2);
        return rangeComparator.compare(r1, r2);
    }
}
