package com.perceptnet.commons.utils;

import java.util.Date;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 08.11.2019
 */
public class CastUtils {

    public static <T> T castUnsafely(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        if (clazz.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }
        if (java.util.Date.class == clazz) {
            if (obj instanceof Long) {
                return (T) new Date((Long) obj);
            } else if (obj instanceof Integer) {
                return (T) new Date(((Integer) obj).longValue());
            }
        } else if (java.sql.Date.class == clazz) {
            if (obj instanceof Long) {
                return (T) new java.sql.Date((Long) obj);
            } else if (obj instanceof Integer) {
                return (T) new java.sql.Date(((Integer) obj).longValue());
            }
        } else if (java.sql.Timestamp.class == clazz) {
            if (obj instanceof Long) {
                return (T) new java.sql.Timestamp((Long) obj);
            } else if (obj instanceof Integer) {
                return (T) new java.sql.Timestamp(((Integer) obj).longValue());
            }
        }

        return ParseUtils.parseUnsafely(obj.toString(), clazz);
    }

}
