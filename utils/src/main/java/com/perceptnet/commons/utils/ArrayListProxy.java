package com.perceptnet.commons.utils;

import com.perceptnet.abstractions.IndexedAccess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vkorovkin on 13.03.2017.
 */
public class ArrayListProxy<T> extends ArrayList<T> implements IndexedAccess<T> {
    public ArrayListProxy(int initialCapacity) {
        super(initialCapacity);
    }

    public ArrayListProxy() {
    }

    public ArrayListProxy(Collection<? extends T> c) {
        super(c);
    }
}
