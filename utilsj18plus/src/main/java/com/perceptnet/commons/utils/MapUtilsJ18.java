package com.perceptnet.commons.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * created by vkorovkin on 14.06.2018
 */
public class MapUtilsJ18 {
    public static <T, K> Map<K, T> map(Collection<T> items, Function<? super T, ? extends K> keyMapper) {
        Map<K, T> result = new HashMap<>(items.size());
        for (T item : items) {
            K key = keyMapper.apply(item);
            result.put(key, item);
        }
        return result;
    }

    public static <T, K> Map<K, T> mapPessimistic(Collection<T> items, Function<? super T, ? extends K> keyMapper) {
        Map<K, T> result = new HashMap<>(items.size());
        for (T item : items) {
            try {
                K key = keyMapper.apply(item);
                result.put(key, item);
            } catch (Exception e) {
                //ignore
            }
        }
        return result;
    }
}
