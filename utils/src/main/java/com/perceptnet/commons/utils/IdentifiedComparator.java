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

import com.perceptnet.abstractions.Identified;

import java.util.Comparator;

public class IdentifiedComparator implements Comparator<Identified<Long>> {

    @Override
    public int compare(Identified<Long> o1, Identified<Long> o2) {
        return o1.getId().compareTo(o2.getId());
    }
}
