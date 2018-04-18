package com.perceptnet.abstractions;

/**
 * Created by vkorovkin on 10.03.2017.
 */
public interface IndexedAccess<T> {
    int size();

    T get(int i);
}
