package com.perceptnet.restclient;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.11.2017
 */
public class RestInvocationException extends RuntimeException {
    private String statusMessage;
    private int statusCode;
    private String responseBody;

    public RestInvocationException(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.responseBody = statusMessage;
    }

    public RestInvocationException(String statusMessage, int statusCode, String responseBody) {
        this.statusMessage = statusMessage;
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

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
