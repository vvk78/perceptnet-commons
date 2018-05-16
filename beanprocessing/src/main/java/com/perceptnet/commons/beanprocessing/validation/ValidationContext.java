package com.perceptnet.commons.beanprocessing.validation;

import com.perceptnet.commons.reflection.FieldReflection;
import com.perceptnet.commons.reflection.ReflectionProvider;

/**
 * created by vkorovkin on 16.05.2018
 */
public class ValidationContext {
    private ReflectionProvider srcReflectionProvider;
    private ReflectionProvider destReflectionProvider;

    private ValidationNode curNode;

    public ValidationContext(ReflectionProvider srcReflectionProvider, ReflectionProvider destReflectionProvider) {
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

    void setRootNode(Object srcObject, Object destObject) {
        if (curNode != null) {
            throw new IllegalStateException("Looks like an attempt to set a root node when another one exists already");
        }
        this.curNode = new ValidationNode(this, srcObject, destObject);
    }

    void pushNode(Object srcObj, Object destObj, FieldReflection entryPoint, String entryPointPostfix) {
        if (curNode == null) {
            throw new IllegalStateException("An attempt to push a child node when there is no parent");
        }
        curNode = new ValidationNode(curNode, srcObj, entryPoint, entryPointPostfix);
    }

    void popNode() {
        curNode = curNode.getParent();
    }

    public ValidationNode getCurNode() {
        return curNode;
    }
}
