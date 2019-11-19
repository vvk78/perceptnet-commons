package com.perceptnet.commons.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 30.01.2018
 */
public class SimpleTypeInfoImpl<T> implements SimpleTypeInfo<T> {
    private Class clazz;
    private List<Type> params;
    private boolean isCollection;
    private SimpleTypeInfo collectionItemInfo;
    private List<? extends SimpleTypeInfo> collectionItemsInfos;
    private boolean isFlat;

    public SimpleTypeInfoImpl(Class<T> clazz) {
        this(clazz, null);
    }

    public SimpleTypeInfoImpl(Class<T> clazz, Type[] params) {
        this.clazz = clazz;
        this.isCollection = Collection.class.isAssignableFrom(clazz);
        this.params = params != null ? Arrays.asList(params) : new ArrayList<Type>(1);
        if (this.isCollection && params != null && params.length > 0 && params[0] instanceof Class) {
            setCollectionItemInfo(new SimpleTypeInfoImpl((Class) params[0]));
        }
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public List<Type> getTypeParams() {
        return params;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public boolean isFlat() {
        return isFlat;
    }

    @Override
    public SimpleTypeInfo getCollectionItemInfo() {
        return collectionItemInfo;
    }

    public SimpleTypeInfoImpl setCollectionItemInfo(SimpleTypeInfo collectionItemInfo) {
        this.collectionItemInfo = collectionItemInfo;
        return this;
    }

    @Override
    public List<? extends SimpleTypeInfo> getCollectionItemsInfos() {
        if (collectionItemsInfos != null) {
            return collectionItemsInfos;
        } else if (isCollection()) {
            return Collections.singletonList(getCollectionItemInfo());
        } else {
            return null;
        }
    }

    public SimpleTypeInfoImpl setCollectionItemsInfos(List<? extends SimpleTypeInfo> collectionItemsInfo) {
        this.collectionItemsInfos = collectionItemsInfo;
        return this;
    }

    public void setFlat(boolean flat) {
        isFlat = flat;
    }

    @Override
    public String toString() {
        return "SimpleTypeInfo{" + clazz + (params.isEmpty() ? "" : ("<" + Joiner.on(",").join(params) +">")) +
                (isCollection() ? (getCollectionItemsInfos() == null ?
                            "; Item: " + collectionItemInfo
                                : "; Items: " + Joiner.on(",").join(getCollectionItemsInfos()) + ""
                        ) : "")
                + '}';
    }



}
