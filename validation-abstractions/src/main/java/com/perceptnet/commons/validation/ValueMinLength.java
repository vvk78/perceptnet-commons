package com.perceptnet.commons.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * created by vkorovkin on 18.05.2018
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD})
public @interface ValueMinLength {
    int value();
}