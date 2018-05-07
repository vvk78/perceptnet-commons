package com.perceptnet.commons;

import com.perceptnet.abstractions.IndexedAccess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.perceptnet.commons.utils.MiscUtils.asList;

/**
 * created by vkorovkin on 03.05.2018
 */
public class BlendedIndexedAccess<T> implements IndexedAccess<T> {
    private SortedMap<Integer, IndexedAccess<T>> index;
    private final int totalSize;

    public BlendedIndexedAccess(IndexedAccess<T> ... items) {
        this(asList(items));
    }

    public BlendedIndexedAccess(Collection<IndexedAccess<T>> items) {
        this.index = new TreeMap<>();
        int curOffset = 0;
        for (IndexedAccess<T> item : items) {
            if (item == null || item.size() == 0) {
                continue;
            }
            index.put(curOffset, item);
            curOffset += item.size();
        }
        this.totalSize = curOffset;
    }

    @Override
    public int size() {
        return totalSize;
    }

    @Override
    public T get(int i) {
        if (i < 0 || i >= totalSize) {
            throw new ArrayIndexOutOfBoundsException("Index " + i + " is out of bounds (0, " + totalSize + ")");
        }
        SortedMap<Integer, IndexedAccess<T>> headMap = index.headMap(i);
        Integer prevOffset = headMap.lastKey();
//        if ()
//        if (prevOffset)
//
//        int startOffset = 0;
//        for (Map.Entry<Integer, IndexedAccess<T>> entry : headMap.entrySet()) {
//            IndexedAccess<T> access = entry.getValue();
//            if (i < startOffset + access.size() ) {
//                return access.get(i - startOffset);
//            }
//            startOffset += entry.getKey();
//        }
//        throw new ArrayIndexOutOfBoundsException("Index " + i + " is out of bounds (0, " + totalSize + ")");
        return null;
    }
}
