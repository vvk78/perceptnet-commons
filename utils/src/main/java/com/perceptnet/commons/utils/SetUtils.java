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

import java.util.Set;

/**
 * Created by vkorovkin on 23.11.2015.
 */
public class SetUtils {

    public static <T> boolean overlaps(Set<T> itemsA, Set<T> itemsB) {
        for (T itemA : itemsA) {
            if (itemsB.contains(itemA)) {
                return true;
            }
        }
        return false;
    }
}
