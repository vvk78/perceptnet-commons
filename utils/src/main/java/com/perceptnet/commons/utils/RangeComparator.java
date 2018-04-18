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

import static com.perceptnet.commons.utils.ComparableUtils.compareNullable;

/**
 * Created by vkorovkin on 17.03.15.
 */
public class RangeComparator implements Comparator<Range<?>> {

    @Override
    public int compare(Range<?> o1, Range<?> o2) {

        int result = compareNullable(o1.getLowBound(), o2.getLowBound(), true);
        if (result == 0) {
            result = compareNullable(o1.getHighBound(), o2.getHighBound(), false);
        }
        return result;
    }


}
