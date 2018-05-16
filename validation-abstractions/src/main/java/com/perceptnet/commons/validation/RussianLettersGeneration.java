package com.perceptnet.commons.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD})
public @interface RussianLettersGeneration {
    int size();
}
