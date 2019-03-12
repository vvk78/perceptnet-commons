package com.perceptnet.abstractions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 30.01.2019
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD})
public @interface UpdatableRelation {
}
