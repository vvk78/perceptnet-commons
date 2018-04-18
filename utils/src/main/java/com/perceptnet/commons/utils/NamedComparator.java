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

import com.perceptnet.abstractions.Named;

import java.util.Comparator;

/**
 * Created by VKorovkin on 06.03.2015.
 */
public class NamedComparator implements Comparator<Named> {
    private final boolean ignoreCase;
    private final boolean nullGreater;

    public NamedComparator(boolean ignoreCase, boolean nullGreater) {
        this.ignoreCase = ignoreCase;
        this.nullGreater = nullGreater;
    }

    @Override
    public int compare(Named o1, Named o2) {
        String n1 = o1.getName();
        String n2 = o2.getName();
        if (n1 == n2) {
            return 0;
        } else if (n1 == null) {
            return nullGreater ? 1 : -1;
        } else if (n2 == null) {
            return nullGreater ? -1 : 1;
        }
        return ignoreCase ? n1.compareToIgnoreCase(n2) : n1.compareTo(n2);
    }
}
