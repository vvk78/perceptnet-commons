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
import com.perceptnet.abstractions.Identified;
import com.perceptnet.abstractions.Named;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Miscellaneous utils
 *
 * Created by VKorovkin on 24.03.2015.
 */
public class MiscUtils {

    public static String getMethodQualifiedSignature(Method m) {
        StringBuilder b = new StringBuilder(m.getName().length() + (m.getParameterTypes().length * 50) + 10);
        b.append(m.getName());
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

    public static int length(Object[] array) {
        return array == null ? 0 : array.length;
    }

    public static void keepOnlyFirstN(Collection items, int n) {
        int count = 0;
        for (Iterator iter = items.iterator(); iter.hasNext(); ) {
            Object next = iter.next();
            count++;
            if (count > n) {
                iter.remove();
            }
        }
    }

    /**
     * This is a convenience method around ConcurrentMap#putIfAbsent but returning actual value after invoking this operation
     * (as it basically should have been done in the ConcurrentMap#putIfAbsent method iteself)
     */
    public static <K, V> V putIfAbsent(ConcurrentHashMap<K, V> map, K key, V value) {
        V prevValue = map.putIfAbsent(key, value);
        return prevValue == null ? value : prevValue;
    }

    public static <ID extends Object, O extends Identified<ID>> Map<ID, O> mapById(Collection<O> objects) {
        Map<ID, O> result = new HashMap<ID, O>(objects.size());
        for (O object : objects) {
            result.put(object.getId(), object);
        }
        return result;
    }

    public static <O extends Named> Map<String, O> mapByName(O[] objects) {
        return mapByName(asList(objects));
    }

    public static <O extends Named> Map<String, O> mapByName(Collection<O> objects) {
        Map<String, O> result = new HashMap<String, O>();
        for (O object : objects) {
            result.put(object.getName(), object);
        }
        return result;
    }

    public static <O extends Named> O firstWithName(String name, Collection<O> objects) {
        for (O object : objects) {
            if (name.equals(object.getName())) {
                return object;
            }
        }
        return null;
    }

    public static <ID, O extends Identified<ID>> O firstWithId(ID id, Collection<O> objects) {
        for (O object : objects) {
            if (id.equals(object.getId())) {
                return object;
            }
        }
        return null;
    }

    public static <CODE extends Comparable<?>, O extends Coded<CODE>> O firstWithCode(CODE code, Collection<O> objects) {
        for (O object : objects) {
            if (code.equals(object.getCode())) {
                return object;
            }
        }
        return null;
    }

    public static <CODE extends Comparable<?>, E extends Enum & Coded<CODE>> E firstEnumWithCode(CODE code, Class<E> enumClass) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (code.equals(e.getCode())) {
                return e;
            }
        }
        return null;
    }

    public static <ID, O extends Identified<ID>> O removeFirstWithId(ID id, Collection<O> objects) {
        for (Iterator<O> iterator = objects.iterator(); iterator.hasNext(); ) {
            O object = iterator.next();
            if (id.equals(object.getId())) {
                iterator.remove();
                return object;
            }
        }
        return null;
    }

    public static <T> List<T> asList(T[] items) {
        //(Arrays.asList is not working as expected on normal array, it returns List<t[]> instead of List<T>)
        List<T> result = new ArrayList<>(items.length);
        for (T item : items) {
            result.add(item);
        }
        return result;
    }

    public static <C extends Comparable<C>> int compare(C c1, C c2, boolean nullGreater) {
        if (c1 == c2) {
            return 0;
        }
        if (c1 == null) {
            return nullGreater ? 1 : -1;
        }
        if (c2 == null) {
            return nullGreater ? -1 : 1;
        }
        return c1.compareTo(c2);
    }

    public static <T> Pair<List<T>, List<T>> split(List<T> list, int tailBeginIndex) {
        if (list.size() <= tailBeginIndex) {
            return new Pair(list, Collections.emptyList());
        } else {
            return new Pair(list.subList(0, tailBeginIndex), list.subList(tailBeginIndex, list.size()));
        }
    }

    public static int byteToLength(byte b) {
        if (b >= 0) {
            return b;
        } else {
            return -b + Byte.MAX_VALUE;
        }
    }

    public static byte lengthToByte(int length) {
        if (length > 255 || length < 0) {
            throw new IllegalArgumentException("Cannot translate to byte, too big length: " + length);
        }
        if (length <= Byte.MAX_VALUE) {
            return (byte) length;
        } else {
            return (byte)(-1 * (length - Byte.MAX_VALUE));
        }
    }

}
