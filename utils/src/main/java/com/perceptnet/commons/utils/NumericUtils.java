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

import java.util.Collection;

/**
 * Created by vkorovkin on 30.07.2015.
 */
public class NumericUtils {

    public static int sum(Collection<Integer> numbers) {
        int result = 0;
        for (Integer number : numbers) {
            result = number != null ? result + number : result;
        }
        return result;
    }

    public static IntNumbersStatistics calcStatistics(Collection<Integer> integers) {
        if (integers == null || integers.isEmpty()) {
            return null;
        }
        IntNumbersStatistics result = new IntNumbersStatistics();
        for (Integer intNum : integers) {
            if (intNum < result.getMinValue()) {
                result.setMinValue(intNum);
            }
            if (intNum > result.getMaxValue()) {
                result.setMaxValue(intNum);
            }
        }
        return result;
    }

    public static boolean notNegative(int val) {
        return val >= 0;
    }


}
