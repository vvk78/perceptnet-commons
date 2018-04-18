package com.perceptnet.commons.reflection;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 23.08.2017
 */
public interface ReflectionProvider {
    BeanReflection getReflection(Class clazz);
}
