package com.perceptnet.restclient;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 13.02.2018
 */
public interface RestCallEventListener {
    void beforeCall();

    void afterCallSuccess();

    void afterCallError(Throwable t);
}
