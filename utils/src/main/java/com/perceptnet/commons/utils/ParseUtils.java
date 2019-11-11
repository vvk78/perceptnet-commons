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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vkorovkin on 12.10.2015.
 */
public class ParseUtils {
    private static final Logger log = LoggerFactory.getLogger(ParseUtils.class);

    private static final Set<String> lowcasedTrueStringsAlternatives =
            new HashSet<String>(Arrays.asList("y", "yes",
//                    "true",
                    "t", "1", "да", "д"));

    private static final Set<String> lowcasedFalseStringsAlternatives =
            new HashSet<String>(Arrays.asList("n", "no", "false", "f", "0", "нет", "н"));

    private static final List<String> SUPPORTED_YEAR_FIRST_DATE_FORMATS =
            Arrays.asList(
                    //short formats
                    "YYYY.MM.dd",
                    "yyyy/MM/dd",
                    "YYYY.MM.dd HH:mm",
                    "yyyy/MM/dd HH:mm",
                    //long formats
                    "yyyy/MM/dd HH:mm:ss",
                    "YYYY.MM.dd HH:mm:ss",
                    "yyyy/MM/dd HH:mm:ss:SSS",
                    "YYYY.MM.dd HH:mm:ss:SSS",
                    "yyyy/dd/MM'T'HH:mm:ss:SSSZ",
                    "yyyy.dd.MM'T'HH:mm:ss:SSSZ"
            );

    private static final List<String> SUPPORTED_DAY_FIRST_DATE_FORMATS =
            Arrays.asList(
                    "dd/MM/yy",
                    "dd.MM.yy",
                    "dd/MM/yyyy",
                    "dd.MM.yyyy",
                    "dd/MM/yyyy HH:mm",
                    "dd.MM.yyyy HH:mm",
                    "dd/MM/yyyy HH:mm:ss",
                    "dd.MM.yyyy HH:mm:ss",
                    "dd/MM/yyyy HH:mm:ss:SSS",
                    "dd.MM.yyyy HH:mm:ss:SSS",
                    "dd/MM/yyyy'T'HH:mm:ss:SSSZ",
                    "dd.MM.yyyy'T'HH:mm:ss:SSSZ"
            );

    private static final List<String> SUPPORTED_MONTH_FIRST_DATE_FORMATS =
            Arrays.asList(
                    "MM/dd/yy",
                    "MM.dd.yy",
                    "MM/dd/yyyy",
                    "MM.dd.yyyy",
                    "MM/dd/yyyy HH:mm",
                    "MM.dd.yyyy HH:mm",
                    "MM/dd/yyyy HH:mm:ss",
                    "MM.dd.yyyy HH:mm:ss",
                    "MM/dd/yyyy HH:mm:ss:SSS",
                    "MM.dd.yyyy HH:mm:ss:SSS",
                    "MM/dd/yyyy'T'HH:mm:ss:SSSZ",
                    "MM.dd.yyyy'T'HH:mm:ss:SSSZ"
            );


    public static <T> T parseSafely(String str, Class<T> clazz) {
        if (String.class.equals(clazz)) {
            return (T) str;
        }
        if (str == null || StringUtils.isBlank(str)) {
            return null;
        }

        try {
            return parseUnsafely(str, clazz);
        } catch (Exception e) {
            log.trace("Cannot parse {} as {} due to {}", str, clazz.getSimpleName(), e, e);
            return null;
        }
    }

    public static <T, E extends Exception> T parseUnsafely(String str, Class<T> valueClass, E exceptionToThrow) throws E {
        try {
            return parseUnsafely(str, valueClass);
        } catch (Exception e) {
            log.debug("Exception while parsing {} as {} due to {}", str, valueClass.getSimpleName(), e, e);
            throw exceptionToThrow;
        }
    }

    public static <T> T parseUnsafely(String str, Class<T> valueClass) {
        if (str == null) {
            throw new NullPointerException("String value is null");
        }
        if (String.class.equals(valueClass)) {
            return (T) str;
        }

        if (Character.class.isAssignableFrom(valueClass) || valueClass == char.class) {
            return (T) new Character(str.charAt(0));
        } else if (Enum.class.isAssignableFrom(valueClass)) {
            Class<Enum> eClass = (Class<Enum>) valueClass;
            return (T) Enum.valueOf(eClass, str);
        } else if (Long.class.equals(valueClass) || long.class == valueClass) {
            return (T) Long.valueOf(str);
        } else if (Integer.class.equals(valueClass) || int.class == valueClass) {
            return (T) Integer.valueOf(str);
        } else if (Short.class.equals(valueClass) || short.class == valueClass) {
            return (T) Short.valueOf(str);
        } else if (Byte.class.equals(valueClass) || byte.class == valueClass) {
            return (T) Byte.valueOf(str);
        } else if (Double.class.equals(valueClass) || double.class == valueClass) {
            return (T) Double.valueOf(str);
        } else if (Float.class.equals(valueClass) || float.class == valueClass) {
            return (T) Float.valueOf(str);
        } else if (Boolean.class.equals(valueClass) || boolean.class == valueClass) {
            //a little more resilient logic than of Boolean.valueOf():
            //in sake of optimization
            if ("true".equalsIgnoreCase(str)) {
                return (T) Boolean.TRUE;
            }
            String lowcasedStr = str.toLowerCase();
            if (lowcasedTrueStringsAlternatives.contains(lowcasedStr)) {
                return (T) Boolean.TRUE;
            } else if (lowcasedFalseStringsAlternatives.contains(lowcasedStr)) {
                return (T) Boolean.FALSE;
            }

            Double doubleVal = parseSafely(str, Double.class);
            if (doubleVal != null) {
                return (T) (Boolean.valueOf(doubleVal.intValue() == 1 || (doubleVal > 0.98 && doubleVal < 1.02)));
            }

            throw new IllegalArgumentException("Cannot parse '" + str + "' as boolean");
        } else if (java.util.Date.class.equals(valueClass) || java.sql.Date.class.equals(valueClass) || Timestamp.class.equals(valueClass)) {
            try {
                Long ms = Long.valueOf(str);
                if (java.sql.Date.class.equals(valueClass)) {
                    return (T) new java.sql.Date(ms);
                } else if (Timestamp.class.equals(valueClass)) {
                    return (T) new Timestamp(ms);
                } else {
                    return (T) new Date(ms);
                }
            } catch (NumberFormatException ignore) {
            }
            Date result = parseDateSafely(str);
            if (result == null) {
                throw new IllegalArgumentException("Cannot parse '" + str + "' as Date");
            }
            if (java.util.Date.class == valueClass) {
                return (T) result;
            } else if (java.sql.Date.class.equals(valueClass)) {
                return (T) new java.sql.Date(result.getTime());
            } else if (Timestamp.class.equals(valueClass)) {
                return (T) new Timestamp(result.getTime());
            } else {
                throw new UnsupportedOperationException("Not supported value class: " + valueClass);
            }
        } else {
            throw new IllegalArgumentException("Not supported value class: " + valueClass);
        }
    }


    private static Date parseDateSafely(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        if (str.isEmpty()) {
            return null;
        }
        int strLength = str.length();
        if (strLength < 4) {
            return null;
        }
        Date result = null;
        try {
            return new SimpleDateFormat().parse(str);
        } catch (ParseException ignore) {
            //
        }

        //try to understand if deal with "year first" or "day first" pattern
        int idx = -1;
        for (int i = 0; i < strLength; i++) {
            char ch = str.charAt(i);
            if (ch == '.' || ch == '/' || Character.isWhitespace(ch)) {
                idx = i;
                break;
            }
            //attention, optimization - may be dangerous, check SUPPORTED_YEAR_FIRST_DATE_FORMATS and SUPPORTED_DAY_FIRST_DATE_FORMATS:
            if (i > 5) {
                break;
            }
        }

        if (idx < 0) {
            return null;
        }
        List<String> formatsList = idx > 2 ? SUPPORTED_YEAR_FIRST_DATE_FORMATS : SUPPORTED_DAY_FIRST_DATE_FORMATS;

        for (String supportedDateFormat : formatsList) {
            if (strLength > supportedDateFormat.length()) {
                continue;
            }
            SimpleDateFormat format = new SimpleDateFormat(supportedDateFormat);
            try {
                result = format.parse(str);
                break;
            } catch (ParseException ignore) {
                //
            }
        }
        return result;
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
