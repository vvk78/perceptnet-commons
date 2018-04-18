/*
 * Copyright 2015 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 *
 */

package com.perceptnet.commons.utils;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by VKorovkin on 16.03.2015.
 */
public class ReflectionUtils {

    public static String getFieldName(Method method) {
        if (method.getName().startsWith("is")) {
            return Introspector.decapitalize(StringUtils.getTail(method.getName(), "is"));
        } else if (method.getName().startsWith("get")) {
            return Introspector.decapitalize(StringUtils.getTail(method.getName(), "get"));
        } else if (method.getName().startsWith("set")) {
            return Introspector.decapitalize(StringUtils.getTail(method.getName(), "set"));
        } else {
            throw new IllegalArgumentException("Does not seem to be getter or setter: " + method);
        }
    }

    public static boolean isGetter(Method method) {
        if (method == null) {
            return false;
        }
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        Class<?> rt = method.getReturnType();
        if (rt == null || void.class.equals(rt) || Void.class.equals(rt)) {
            return false;
        }
        if ((rt.equals(Boolean.class) || rt.equals(boolean.class))) {
            if (method.getName().startsWith("is") && !StringUtils.getTail(method.getName(), "is").isEmpty()) {
                return true;
            }
        }

        if (method.getName().startsWith("get")
                && !StringUtils.getTail(method.getName(), "get").isEmpty()) {
            return true;
        }

        return false;
    }

    public static boolean isSetter(Method method) {
        if (method == null) {
            return false;
        }
        if (method.getParameterTypes().length != 1) {
            return false;
        }
        Class<?> rt = method.getReturnType();
        if (!void.class.equals(rt)) {
            return false;
        }


        if (method.getName().startsWith("set")
                && !StringUtils.getTail(method.getName(), "set").isEmpty()) {
            return true;
        }

        return false;
    }

    public static Class getCollectionItemClassFromGetter(Method getter) {
        Class returnClass = getter.getReturnType();
        if (isCollection(returnClass)) {
            Type returnType = getter.getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) returnType;
                Type[] argTypes = paramType.getActualTypeArguments();
                if (argTypes.length > 0) {
                    return (Class) argTypes[0];
                }
            }
        }
        return null;
    }

    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive();
    }


}
