package com.perceptnet.commons.reflection;

import java.lang.reflect.Method;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 09.01.2018
 */
public interface ReflectionBuilderMethodFilter {
    boolean isMethodToBeProcessed(Method m);
}
