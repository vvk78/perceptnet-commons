package com.perceptnet.commons.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 *
 * created by vkorovkin on 16.05.2018
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD})
public @interface ValueMaxLength {
    int value();
}
