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

/**
 * Created by vkorovkin on 13.08.2015.
 */
public class IntNumbersStatistics {
    private int minValue;
    private int maxValue;

    public IntNumbersStatistics() {
        //not calculated state
        minValue = Integer.MAX_VALUE;
        maxValue = Integer.MIN_VALUE;
    }

    public IntNumbersStatistics(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isCalculated() {
        return minValue <= maxValue;
    }


}
