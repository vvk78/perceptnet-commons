package com.perceptnet.restclient;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 27.11.2017
 */
public class BaseRestMethodRegistry implements RestMethodDescriptionProvider {
    /**
     * Initial "slow" map of string class names and then sting method signatures on RestMethod descriptions.
     * This map is initially filled in source code produced by code generator. Gradually its content migrates to
     * fastMap as method names get resolved. Finally, when there is not content left, the map is made null to be GC-ed
     */
    private volatile ConcurrentHashMap<String, ServiceMethodsRegistry> slowMap;

    private final ConcurrentHashMap<Method, RestMethodDescription> fastMap;

    public BaseRestMethodRegistry(int totalMethodsCount, ConcurrentHashMap<String, ServiceMethodsRegistry> slowMap) {
        this.fastMap = new ConcurrentHashMap<>(totalMethodsCount);
        this.slowMap = slowMap;
    }

    public RestMethodDescription getMethodDescription(Method m) {
        RestMethodDescription result = fastMap.get(m);
        if (result != null) {
            return result;
        }

        final String serviceClassName = m.getDeclaringClass().getName();
        final String mSignature = m.toString();
        if (slowMap == null) {
            throw new UnsupportedOperationException("Service method " + mSignature + " of " + serviceClassName + " is not supported");
        }

        final ServiceMethodsRegistry subMap = slowMap.get(serviceClassName);
        if (subMap == null) {
            throw new UnsupportedOperationException("Service method " + mSignature + " of " + serviceClassName + " is not supported");
        }
        result = subMap.getMethodDescription(m);
        if (result == null) {
            throw new UnsupportedOperationException("Service method " + mSignature + " of " + serviceClassName + " is not supported");
        }
        if (fastMap.putIfAbsent(m, result) == null) {
            if (subMap.remove(m) && subMap.isEmpty()) {
                if (slowMap.remove(serviceClassName) != null && slowMap.isEmpty()) {
                    slowMap = null;
                }
            }
        }
        return result;
    }

}
