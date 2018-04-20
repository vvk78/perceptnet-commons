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

package com.perceptnet.commons.beanprocessing.conversion;

import com.perceptnet.commons.beanprocessing.BeanKey;
import com.perceptnet.commons.reflection.FieldReflection;
import com.perceptnet.commons.reflection.ReflectionProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vkorovkin on 22.03.15.
 */
public class ConversionContext {

    private ReflectionProvider srcReflectionProvider;
    private ReflectionProvider destReflectionProvider;

    private Map<BeanKey, Object> resolvedDestObjects = new HashMap<>();

    private ConversionNode curNode;

    public ConversionContext(ReflectionProvider srcReflectionProvider, ReflectionProvider destReflectionProvider) {
        if (srcReflectionProvider == null) {
            throw new NullPointerException("SrcReflectionProvider is null");
        }
        if (destReflectionProvider == null) {
            throw new NullPointerException("DestReflectionProvider is null");
        }
        this.srcReflectionProvider = srcReflectionProvider;
        this.destReflectionProvider = destReflectionProvider;
    }

    public ReflectionProvider getSrcReflectionProvider() {
        return srcReflectionProvider;
    }

    public ReflectionProvider getDestReflectionProvider() {
        return destReflectionProvider;
    }

    public Map<BeanKey, Object> getResolvedDestObjects() {
        return resolvedDestObjects;
    }

    void setRootNode(Object srcObject, Object destObject) {
        if (curNode != null) {
            throw new IllegalStateException("Looks like an attempt to set a root node when another one exists already");
        }
        this.curNode = new ConversionNode(this, srcObject, destObject);
    }

    void pushNode(Object srcObj, Object destObj, FieldReflection entryPoint, String entryPointPostfix) {
        if (curNode == null) {
            throw new IllegalStateException("An attempt to push a child node when there is no parent");
        }
        curNode = new ConversionNode(curNode, srcObj, destObj, entryPoint, entryPointPostfix);
    }

    void popNode() {
        curNode = curNode.getParent();
    }

    public ConversionNode getCurNode() {
        return curNode;
    }
}
