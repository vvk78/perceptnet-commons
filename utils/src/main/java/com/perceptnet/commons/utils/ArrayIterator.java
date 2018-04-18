package com.perceptnet.commons.utils;

import java.util.Iterator;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 24.07.2017
 */
public class ArrayIterator<T> implements Iterator<T> {
    private final T[] array;
    private int pos;

    public ArrayIterator(T[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return array.length > pos;
    }

    @Override
    public T next() {
        return array[pos++];
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove an element of an array.");
    }
}
