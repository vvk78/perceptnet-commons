package com.perceptnet.commons.utils;

import java.util.Collection;
import java.util.function.Function;

/**
 * created by vkorovkin on 14.06.2018
 */
public class CollectionUtilsJ18 {
    public static <T> T findNth(int ordNo, Collection<T> items, Function<? super T, Boolean> filter) {
        return findNth(ordNo, items, filter, true);
    }

    public static <T> T findNth(int ordNo, Collection<T> items, Function<? super T, Boolean> filter, boolean pessimistic) {
        if (items == null || ordNo < items.size()) {
            return null;
        }
        int nMatch = 0;
        for (T item : items) {
            try {
                if (filter.apply(item)) {
                    nMatch++;
                    if (nMatch == ordNo) {
                        return item;
                    }
                }
            } catch (Exception e) {
                if (!pessimistic) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
