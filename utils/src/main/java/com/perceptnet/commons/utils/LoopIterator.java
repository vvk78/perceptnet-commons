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

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * Created by vkorovkin on 03.07.2015.
 */
public class LoopIterator<E> implements Iterator<E> {

    private final Collection<E> onCollection;
    private Iterator<E> curIterator;

    public LoopIterator(Collection<E> onCollection) {
        if (onCollection == null) {
            throw new NullPointerException("Collection is null");
        }
        this.onCollection = onCollection;
    }

    @Override
    public boolean hasNext() {
        return curIterator.hasNext() || !onCollection.isEmpty();
    }

    @Override
    public E next() {
        if (curIterator == null || !curIterator.hasNext()) {
            curIterator = onCollection.iterator();
        }
        return curIterator.next();
    }
}
