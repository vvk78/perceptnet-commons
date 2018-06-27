package com.perceptnet.restclient;

import java.lang.reflect.Method;

/**
 * created by vkorovkin on 27.06.2018
 */
public interface RestInvocationErrorHandler {
    /**
     * Handle rest invocation exception for a method
     * @param converter message converter used by rest handler to convert server responses. This handler is free to use it or not
     * @param e exception to handle
     * @param m the method the rest call was made for
     */
    void handle(RestInvocationException e, MessageConverter converter, Method m) throws Throwable;
}
