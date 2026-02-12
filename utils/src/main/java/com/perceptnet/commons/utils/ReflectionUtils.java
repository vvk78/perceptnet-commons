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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by VKorovkin on 16.03.2015.
 */
public class ReflectionUtils {
    public static String getMethodKey(Method m) {
        return m.getName() + getMethodQualifiedBracedSignature(m);
    }

    public static String getMethodQualifiedBracedSignature(Method m) {
        StringBuilder b = new StringBuilder(m.getName().length() + (m.getParameterTypes().length * 50) + 10);
        b.append("(");
        boolean first = true;
        for (Class<?> aClass : m.getParameterTypes()) {
            if (!first) {
                b.append(",");
            } else {
                first = false;
            }
            b.append(aClass.getName());
        }
        b.append(")");
        return b.toString();
    }

    public static String getFieldName(Method method) {
        if (method.getName().startsWith("is")) {
            return decapitalizeIfNeeded(StringUtils.getTail(method.getName(), "is"));
        } else if (method.getName().startsWith("get")) {
            return decapitalizeIfNeeded(StringUtils.getTail(method.getName(), "get"));
        } else if (method.getName().startsWith("set")) {
            return decapitalizeIfNeeded(StringUtils.getTail(method.getName(), "set"));
        } else {
            throw new IllegalArgumentException("Does not seem to be getter or setter: " + method);
        }
    }

    private static String decapitalizeIfNeeded(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
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

        //return type may be used in 'setters chain' pattern
//        Class<?> rt = method.getReturnType();
//        if (!void.class.equals(rt)) {
//            return false;
//        }


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

    public static Class getCollectionItemClassFromSetter(Method setter) {
        Class<?>[] params = setter.getParameterTypes();
        if (params.length != 1) {
            throw new IllegalArgumentException("Passed method id not setter");
        }
        Class valueClass = params[0];
        if (isCollection(valueClass)) {
            Type valueType = setter.getGenericParameterTypes()[0];
            if (valueType instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) valueType;
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
