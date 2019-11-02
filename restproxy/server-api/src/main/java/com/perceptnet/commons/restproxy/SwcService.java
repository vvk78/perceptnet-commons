package com.perceptnet.commons.restproxy;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 02.11.2019
 */
public interface SwcService {

    Object callService(String requestPath, String requestBody);

}
