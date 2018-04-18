package com.perceptnet.restclient;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.11.2017
 */
public interface RestCaller {
    String invokeRest(RestRequest request) throws RestInvocationException;

    byte[] invokeRestForBytes(RestRequest request) throws RestInvocationException;

}
