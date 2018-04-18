package com.perceptnet.restclient;

import java.util.Date;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 22.12.2017
 */
public class SimplisticMessageConverter implements MessageConverter {

    public <T> T parse(Class<T> expectedType, String str) {
        if (str == null) {
            return null;
        }
        if (String.class.equals(expectedType)) {
            return (T) str;
        }
        if (str.equals("null")) {
            return null;
        } else if (str.isEmpty()) {
            return null;
        }
        try {
            if (Byte.class.equals(expectedType)) {
                return (T) Byte.valueOf(str);
            } else if (Short.class.equals(expectedType)) {
                return (T) Short.valueOf(str);
            } else if (Integer.class.equals(expectedType)) {
                return (T) Integer.valueOf(str);
            } else if (Long.class.equals(expectedType)) {
                return (T) Long.valueOf(str);
            } else if (Short.class.equals(expectedType)) {
                return (T) Short.valueOf(str);
            } else if (Double.class.equals(expectedType)) {
                return (T) Double.valueOf(str);
            } else if (Float.class.equals(expectedType)) {
                return (T) Float.valueOf(str);
            } else if (Float.class.equals(expectedType)) {
                return (T) Float.valueOf(str);
            } else if (Boolean.class.equals(expectedType)) {
                return (T) Boolean.valueOf(str);
            } else if (java.sql.Timestamp.class.equals(expectedType)) {
                return (T) new java.sql.Timestamp(Long.valueOf(str));
            } else if (java.sql.Date.class.equals(expectedType)) {
                return (T) new java.sql.Date(Long.valueOf(str));
            } else if (Date.class.equals(expectedType)) {
                return (T) new Date(Long.valueOf(str));
            } else {
                throw new UnsupportedOperationException("" + expectedType + " is not supported");
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse '" + str + "' as " + expectedType.getSimpleName() + " due to " + e, e);
        }
    }

    @Override
    public String format(Object obj) {
        return "" + obj;
    }
}
