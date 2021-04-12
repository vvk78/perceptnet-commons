package com.perceptnet.restclient;

import com.perceptnet.commons.utils.SimpleTypeInfo;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 22.12.2017
 */
public class SimplisticMessageConverter implements MessageConverter {

    public <T> T parse(Type expectedType, String str) {
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
        Class<T> expectedClass = null;
        if (expectedClass instanceof Class) {
            expectedClass = (Class<T>) expectedType;
        } else if (expectedType instanceof SimpleTypeInfo) {
            expectedClass = ((SimpleTypeInfo) expectedType).getClazz();
        } else {
            throw new UnsupportedOperationException("Unsupported type info: " + expectedType);
        }

        try {
            if (Byte.class.equals(expectedClass)) {
                return (T) Byte.valueOf(str);
            } else if (Short.class.equals(expectedClass)) {
                return (T) Short.valueOf(str);
            } else if (Integer.class.equals(expectedClass)) {
                return (T) Integer.valueOf(str);
            } else if (Long.class.equals(expectedClass)) {
                return (T) Long.valueOf(str);
            } else if (Short.class.equals(expectedClass)) {
                return (T) Short.valueOf(str);
            } else if (Double.class.equals(expectedClass)) {
                return (T) Double.valueOf(str);
            } else if (Float.class.equals(expectedClass)) {
                return (T) Float.valueOf(str);
            } else if (Float.class.equals(expectedClass)) {
                return (T) Float.valueOf(str);
            } else if (Boolean.class.equals(expectedClass)) {
                return (T) Boolean.valueOf(str);
            } else if (java.sql.Timestamp.class.equals(expectedClass)) {
                return (T) new java.sql.Timestamp(Long.valueOf(str));
            } else if (java.sql.Date.class.equals(expectedClass)) {
                return (T) new java.sql.Date(Long.valueOf(str));
            } else if (Date.class.equals(expectedClass)) {
                return (T) new Date(Long.valueOf(str));
            } else {
                throw new UnsupportedOperationException("" + expectedClass + " is not supported");
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse '" + str + "' as " + expectedClass.getSimpleName() + " due to " + e, e);
        }
    }

    @Override
    public String format(Object obj) {
        return "" + obj;
    }
}
