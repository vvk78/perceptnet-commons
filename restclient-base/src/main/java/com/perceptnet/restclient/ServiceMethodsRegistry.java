package com.perceptnet.restclient;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 07.12.2017
 */
public class ServiceMethodsRegistry {
    /**
     * This map maps method name with flat signature on description
     */
    private ConcurrentHashMap<String, RestMethodDescription> overloadedMethods;
    /**
     * This map maps method name on description
     */
    private ConcurrentHashMap<String, RestMethodDescription> methods;

    public ServiceMethodsRegistry(ConcurrentHashMap<String, RestMethodDescription> overloadedMethods, ConcurrentHashMap<String, RestMethodDescription> methods) {
        if (overloadedMethods == null && methods == null) {
            throw new IllegalArgumentException("Both arguments (overloadedMethods and methods) cannot be nulls simultaneously");
        }
        this.overloadedMethods = overloadedMethods;
        this.methods = methods;
    }

    public RestMethodDescription getMethodDescription(Method m) {
        RestMethodDescription result = methods == null ? null : methods.get(m.getName());
        if (result != null) {
            return result;
        }
        if (overloadedMethods != null && !overloadedMethods.isEmpty()) {
            result = overloadedMethods.get(getMethodNameWithFlatSignature(m));
        }
        return result;
    }

    public boolean remove(Method m) {
        return methods != null && methods.remove(m.getName()) != null
                || overloadedMethods != null && overloadedMethods.remove(getMethodNameWithFlatSignature(m)) != null;
    }

    public boolean isEmpty() {
        return (methods == null || methods.isEmpty()) && (overloadedMethods == null || overloadedMethods.isEmpty());
    }

    private String getMethodNameWithFlatSignature(Method m) {
        StringBuilder buff = new StringBuilder();
        buff.append(m.getName());
        buff.append("(");
        boolean first = true;
        for (Class<?> paramType : m.getParameterTypes()) {
            if (!first) {
                buff.append(",");
            }
            buff.append(paramType.getSimpleName());
        }
        buff.append(")");
        return buff.toString();
    }

}
