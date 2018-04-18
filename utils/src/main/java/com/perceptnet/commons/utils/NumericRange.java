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

/**
 * Created by VKorovkin on 12.05.2015.
 */
public class NumericRange<N extends Number & Comparable> implements Range<N> {
    private final N lowBound;
    private final N highBound;

    public NumericRange(N lowBound, N highBound) {
        if (lowBound != null && highBound != null && lowBound.doubleValue() > highBound.doubleValue()) {
            throw new IllegalArgumentException("Low and high bounds mismatch");
        }
        this.lowBound = lowBound;
        this.highBound = highBound;
    }

    @Override
    public N getLowBound() {
        return lowBound;
    }

    @Override
    public N getHighBound() {
        return highBound;
    }

    public boolean isValueInside(N value) {
        return RangeUtils.isValueInside(this, value);
    }

    @Override
    public String toString() {
        return lowBound + "-" + highBound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumericRange<?> that = (NumericRange<?>) o;

        if (lowBound != null ? !lowBound.equals(that.lowBound) : that.lowBound != null) return false;
        return !(highBound != null ? !highBound.equals(that.highBound) : that.highBound != null);

    }

    @Override
    public int hashCode() {
        int result = lowBound != null ? lowBound.hashCode() : 0;
        result = 31 * result + (highBound != null ? highBound.hashCode() : 0);
        return result;
    }
}
