package com.perceptnet.commons.utils;

import java.util.Iterator;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 24.07.2017
 */
public class ArrayIterable<T> implements Iterable<T> {
    private final T[] array;

    public ArrayIterable(T[] array) {
        this.array = array;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator<>(array);
    }
}
