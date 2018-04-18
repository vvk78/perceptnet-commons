package com.perceptnet.restclient;

import java.lang.reflect.Method;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 27.11.2017
 */
public interface RestMethodDescriptionProvider {
    RestMethodDescription getMethodDescription(Method m);
}
