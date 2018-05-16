package com.perceptnet.commons.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD})
public @interface ValuePattern {
    String value();
    String valueHint() default "";

}
