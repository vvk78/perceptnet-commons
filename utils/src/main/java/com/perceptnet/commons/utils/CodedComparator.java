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

import com.perceptnet.abstractions.Coded;

import java.util.Comparator;

/**
 * Created by VKorovkin on 12.05.2015.
 */
public class CodedComparator implements Comparator<Coded<?>>  {
    private final boolean nullGreater;

    public CodedComparator(boolean nullGreater) {
        this.nullGreater = nullGreater;
    }

    @Override
    public int compare(Coded<?> o1, Coded<?> o2) {
        return ComparableUtils.compareNullable(o1.getCode(), o2.getCode(), nullGreater);
    }
}
