package com.perceptnet.commons.json.parsing;

import com.perceptnet.commons.utils.Joiner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 30.01.2018
 */
public class ObjectInfoImpl implements ObjectInfo {
    private Class clazz;
    private List<Type> params;
    private boolean isCollection;
    private ObjectInfo collectionItemInfo;
    private List<? extends ObjectInfo> collectionItemsInfos;
    private boolean isFlat;

    public ObjectInfoImpl(Class clazz) {
        this(clazz, null);
    }

    public ObjectInfoImpl(Class clazz, Type[] params) {
        this.clazz = clazz;
        this.isCollection = Collection.class.isAssignableFrom(clazz);
        this.params = params != null ? Arrays.asList(params) : new ArrayList<Type>(1);
        if (this.isCollection && params != null && params.length > 0 && params[0] instanceof Class) {
            setCollectionItemInfo(new ObjectInfoImpl((Class) params[0]));
        }
    }

    public Class getClazz() {
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
    public ObjectInfo getCollectionItemInfo() {
        return collectionItemInfo;
    }

    public ObjectInfoImpl setCollectionItemInfo(ObjectInfo collectionItemInfo) {
        this.collectionItemInfo = collectionItemInfo;
        return this;
    }

    @Override
    public List<? extends ObjectInfo> getCollectionItemsInfos() {
        return collectionItemsInfos;
    }

    public ObjectInfoImpl setCollectionItemsInfos(List<? extends ObjectInfo> collectionItemsInfo) {
        this.collectionItemsInfos = collectionItemsInfo;
        return this;
    }

    public void setFlat(boolean flat) {
        isFlat = flat;
    }

    @Override
    public String toString() {
        return "ObjectInfo{" + clazz + (params.isEmpty() ? "" : ("<" + Joiner.on(",").join(params) +">")) +
                (isCollection() ?
                        ", Item: " + collectionItemInfo : "") +
                '}';
    }



}
