package com.perceptnet.restclient;

/**
 * Responsible for formatting POST request body and parsing REST response.
 *
 * created by vkorovkin (vkorovkin@gmail.com) on 25.12.2017
 */
public interface MessageConverter {
    <T> T parse(Class<T> expectedType, String str);

    String format(Object obj);
}
