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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by VKorovkin on 07.04.2015.
 */
public class EnumUtils {
    private static final Logger log = LoggerFactory.getLogger(EnumUtils.class);

    public static <E extends Enum> E parseSafely(Class<E> enumClass, String str) {
        try {
            return parseUnsafely(enumClass, str);
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("Cannot parse {} as enum {} due to {}", str, enumClass.getName(), e, e);
            }
            return null;
        }
    }

    public static <E extends Enum> E parseUnsafely(Class<E> enumClass, String str) {
        if (str == null) {
            return null;
        }
        try {
            return (E) Enum.valueOf(enumClass, str);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot convert '" + str + "' to " + enumClass + " due to: " + e, e);
        }
    }

    public static <CODE extends Comparable<?>, E extends Enum & Coded<CODE>> E firstEnumWithCode(CODE code, Class<E> enumClass) {
        return MiscUtils.firstEnumWithCode(code, enumClass);
    }

}
