package com.perceptnet.commons.beanprocessing.validation;

import com.perceptnet.commons.reflection.FieldReflection;
import com.perceptnet.commons.reflection.ReflectionProvider;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * created by vkorovkin on 16.05.2018
 */
public class ValidationContext implements com.perceptnet.commons.validation.ValidationContext {
    private ReflectionProvider srcReflectionProvider;
    private ReflectionProvider destReflectionProvider;

    private ValidationNode curNode;
    private Set processedObjects = Collections.newSetFromMap(new IdentityHashMap());

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

    public Set getProcessedObjects() {
        return processedObjects;
    }

    public ReflectionProvider getSrcReflectionProvider() {
        return srcReflectionProvider;
    }

    public ReflectionProvider getDestReflectionProvider() {
        return destReflectionProvider;
    }

    void setRootNode(Object srcObject, Class destObjClass) {
        if (curNode != null) {
            throw new IllegalStateException("Looks like an attempt to set a root node when another one exists already");
        }
        this.curNode = new ValidationNode(this, srcObject, destReflectionProvider.getReflection(destObjClass));
    }

    void pushNode(Object srcObj, Class destObjClass, FieldReflection entryPoint, String entryPointPostfix) {
        if (curNode == null) {
            throw new IllegalStateException("An attempt to push a child node when there is no parent");
        }
        curNode = new ValidationNode(curNode, srcObj, destReflectionProvider.getReflection(destObjClass), entryPoint, entryPointPostfix);
    }

    void popNode() {
        curNode = curNode.getParent();
    }

    public ValidationNode getCurNode() {
        return curNode;
    }

    @Override
    public void registerProblem(String path, String problem) {
        //todo implement
    }
}
