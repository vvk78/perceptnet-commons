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

import com.perceptnet.abstractions.Identified;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by VKorovkin on 15.05.2015.
 */
public class MapUtils {


    public static <K, V> Map<K, V> fillMap(Map<K, V> m, Entry<K, V>... entries) {
        for (Entry<K, V> entry : entries) {
            m.put(entry.getKey(), entry.getValue());
        }
        return m;
    }

    public static <K, V> Entry<K, V> entry(K key, V value) {
        return new Entry(key, value);
    }

    public static final class Entry<K, V> {
        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    public static <T> Map<Integer, List<T>> paginateList(List<T> list, int firstPageItemCount, int... nextPagesItemCount) {
        int lastIndex = 0;
        int pageCounter = 0;
        Map<Integer, List<T>> res = new HashMap<>();
        while (lastIndex < list.size()  || lastIndex == 0) {
            int prevIndex = lastIndex;
            lastIndex = pageCounter == 0 || nextPagesItemCount == null || nextPagesItemCount.length == 0
                    ? lastIndex + firstPageItemCount
                    : lastIndex + nextPagesItemCount[Math.min(pageCounter - 1, nextPagesItemCount.length - 1)];
            res.put(++pageCounter, list.subList(prevIndex, Math.min(lastIndex, list.size())));
        }
        return res;
    }

    public static <K,V> Map<V,K> changeKeyAndValues(Map<K,V> map){
        if (map==null){
            return null;
        }
        Map<V,K> res = new HashMap<>(map.size());
        for (K key: map.keySet()){
            res.put(map.get(key), key);
        }
        return res;
    }

    public static<ID, IDD extends Identified<ID>> Map<ID, IDD> mapById(Collection<IDD> items, Map map) {
        for (Object item : items) {
            map.put(((Identified) item).getId(), item);
        }
        return map;
    }

    /**
     * This is a handy method like Map#putIfAbsent but returning actual value associated with the key after method invocation.
     * (as it basically should have been done in original Map#putIfAbsent method)
     */
    public static <K, V> V putIfAbsent(Map<K, V> map, K key, V value) {
        V prevValue = map.putIfAbsent(key, value);
        return prevValue == null ? value : prevValue;
    }

}
