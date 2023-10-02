package com.perceptnet.commons.reflection;



import com.perceptnet.commons.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created by vkorovkin on 27.06.2018
 */
public class MethodsDescriptionRegistry<MD> {
    private transient ConcurrentHashMap<Method, MD> fastMap;
    private Map<String, MD> slowMap = new HashMap();

    public MethodsDescriptionRegistry(Map<String, MD> slowMap) {
        this.slowMap = slowMap;
    }

    protected String methodKey(Method m) {
        return ReflectionUtils.getMethodKey(m);
    }
}
