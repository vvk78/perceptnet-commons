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
 * Created by vkorovkin on 17.03.15.
 */
public class ComparableUtils {

    public static boolean gt(Comparable a, Comparable b) {
        return a.compareTo(b) > 0;
    }

    public static boolean ge(Comparable a, Comparable b) {
        return a.compareTo(b) >= 0;
    }

    public static boolean lt(Comparable a, Comparable b) {
        return a.compareTo(b) < 0;
    }

    public static boolean le(Comparable a, Comparable b) {
        return a.compareTo(b) <= 0;
    }

    public static <C extends Comparable> int compareNullable(C c1, C c2, boolean nullGreater) {
        if (c1 == c2) {
            return 0;
        } else if (c1 == null) {
            return nullGreater ? 1 : -1;
        } else if (c2 == null) {
            return nullGreater ? -1 : 1;
        }
        return c1.compareTo(c2);
    }

}
