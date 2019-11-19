package com.perceptnet.commons.utils;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Simplification over java reflection and types.
 *
 * created by vkorovkin (vkorovkin@gmail.com) on 30.01.2018
 */
public interface SimpleTypeInfo<T> extends Type {
    /**
     * Returns type class
     */
    Class<T> getClazz();

    /**
     * Returns class type params
     */
    List<Type> getTypeParams();

    /**
     * May return true not only for flat item (primitive), but for collection with flat items
     */
    boolean isFlat();

    /**
     * Returns true for collection types
     */
    boolean isCollection();

    /**
     * If collection, return type info for its item
     */
    SimpleTypeInfo getCollectionItemInfo();

    /**
     * If collection has items of different but known types at certain positions, returns their ordered descriptors. Returns null otherwise.
     */
    List<? extends SimpleTypeInfo> getCollectionItemsInfos();

}
