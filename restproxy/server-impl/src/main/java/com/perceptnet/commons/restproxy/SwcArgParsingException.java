package com.perceptnet.commons.restproxy;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 29.01.2018
 */
public class SwcArgParsingException extends Exception {

    public SwcArgParsingException() {
    }

    public SwcArgParsingException(String message) {
        super(message);
    }

    public SwcArgParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SwcArgParsingException(Throwable cause) {
        super(cause);
    }

    public SwcArgParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
