package com.perceptnet.commons.json.parsing;

import java.lang.reflect.Type;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 30.01.2018
 */
public interface ObjectInfo {
    Class getClazz();

    List<Type> getTypeParams();

    boolean isFlat();

    void setFlat(boolean value);

    boolean isCollection();

    ObjectInfo getCollectionItemInfo();

    List<? extends ObjectInfo> getCollectionItemsInfos();

}
