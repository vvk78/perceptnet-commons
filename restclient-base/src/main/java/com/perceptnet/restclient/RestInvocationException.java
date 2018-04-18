package com.perceptnet.restclient;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.11.2017
 */
public class RestInvocationException extends RuntimeException {
    private String responseBody;
    private int statusCode;

    public RestInvocationException(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public RestInvocationException(String message) {
        super(message);
    }

    public RestInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getResponseBody() {
        return responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
