package com.perceptnet.commons.restproxy;

import java.util.List;
import java.util.Map;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 25.01.2018
 */
public class ServiceDescription {
    private Class serviceClass;
    private Object serviceImpl;
    private Map<String, List<MethodDescription>> methods;

    public ServiceDescription(Class serviceClass, Object serviceImpl, Map<String, List<MethodDescription>> methods) {
        this.serviceClass = serviceClass;
        this.serviceImpl = serviceImpl;
        this.methods = methods;
    }

    public Class getServiceClass() {
        return serviceClass;
    }

    public Object getServiceImpl() {
        return serviceImpl;
    }

    public Map<String, List<MethodDescription>> getMethods() {
        return methods;
    }
}
