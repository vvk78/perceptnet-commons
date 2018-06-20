package com.perceptnet.restclient;

import com.perceptnet.restclient.dto.RestMethodDescription;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 07.12.2017
 * edited by villarionov
  */
public class ServiceMethodsRegistry {
    /**
     * This map maps method name on description
     */
    private ConcurrentHashMap<String, RestMethodDescription> methods;


    public ServiceMethodsRegistry(Map<String, RestMethodDescription> methods) {
        if (methods == null) {
            throw new NullPointerException("Methods is null");
        }
        this.methods = new ConcurrentHashMap<String, RestMethodDescription>(methods);
    }

    public ConcurrentHashMap<String, RestMethodDescription> getMethods() {
        return methods;
    }

    public RestMethodDescription getMethodDescription(Method m) {
        return methods == null || m == null ? null : methods.get(methodKey(m));
    }

    public boolean remove(Method m) {
        return methods != null && m != null && methods.remove(methodKey(m)) != null;
    }

    public boolean isEmpty() {
        return (methods == null || methods.isEmpty());
    }

//    private String getMethodNameWithFlatSignature(Method m) {
//        StringBuilder buff = new StringBuilder();
//        buff.append(m.getName());
//        buff.append("(");
//        boolean first = true;
//        for (Class<?> paramType : m.getParameterTypes()) {
//            if (!first) {
//                buff.append(",");
//            }
//            buff.append(paramType.getSimpleName());
//        }
//        buff.append(")");
//        return buff.toString();
//    }

    private String methodKey(Method m) {
        return getMethodQualifiedSignature(m);
    }

    /**
     * copy-pasted from ReflectionUtils (in order not to add the whole dependency)
     */
    private String getMethodQualifiedSignature(Method m) {
        StringBuilder b = new StringBuilder(m.getName().length() + (m.getParameterTypes().length * 50) + 10);
        b.append(m.getName());
        b.append("(");
        boolean first = true;
        for (Class<?> aClass : m.getParameterTypes()) {
            if (!first) {
                b.append(",");
            } else {
                first = false;
            }
            b.append(aClass.getName());
        }
        b.append(")");
        return b.toString();
    }

}
