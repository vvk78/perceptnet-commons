package com.perceptnet.commons.utils;

import com.perceptnet.abstractions.IndexedAccess;

/**
 * created by vkorovkin on 26.04.2018
 */
public class ArrayIndexedAccessProxy<T> implements IndexedAccess<T> {
    private final T[] items;

    public ArrayIndexedAccessProxy(T[] items) {
        this.items = items;
    }

    @Override
    public int size() {
        return items.length;
    }

    @Override
    public T get(int i) {
        return items[i];
    }
}
