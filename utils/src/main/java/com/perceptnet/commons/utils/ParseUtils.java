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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vkorovkin on 12.10.2015.
 */
public class ParseUtils {
    protected static final Logger log = LoggerFactory.getLogger(ParseUtils.class);

    public static <T> T parseSafely(String str, Class<T> clazz) {
        try {
            return parseUnsafely(str, clazz);
        } catch (Exception e) {
            log.trace("Cannot parse " + str + " as " + clazz.getSimpleName(), e);
            return null;
        }
    }

    public static <T> T parseUnsafely(String str, Class<T> clazz) {
        if (str == null) {
            return null;
        }
        if (String.class.equals(clazz)) {
            return (T) str;
        }
        try {
            if (Integer.class.equals(clazz)) {
                return (T) Integer.valueOf(str);
            } else if (Long.class.equals(clazz)) {
                return (T) Long.valueOf(str);
            } else if (Short.class.equals(clazz)) {
                return (T) Short.valueOf(str);
            } else if (Double.class.equals(clazz)) {
                return (T) Double.valueOf(str);
            } else if (Float.class.equals(clazz)) {
                return (T) Float.valueOf(str);
            } else {
                throw new UnsupportedOperationException("Not implemented yet");
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse '" + str + "' as " + clazz.getSimpleName() +" due to " + e, e);
        }
    }

    public static Date parseDateSafely(String str, SimpleDateFormat sdf) {
        try {
            return parseDateUnsafely(str, sdf);
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("Cannot parse " + str + " as date with " + sdf + " due to " + e, e);
            }
            return null;
        }
    }

    public static Date parseDateUnsafely(String str, SimpleDateFormat sdf) {
        if (str == null) {
            return null;
        }
        try {
            return sdf.parse(str);
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse '" + str + "' as date with " + sdf + " due to " + e, e);
        }
    }

}
