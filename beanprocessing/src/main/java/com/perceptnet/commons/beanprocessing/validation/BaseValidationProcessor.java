package com.perceptnet.commons.beanprocessing.validation;

import com.perceptnet.commons.reflection.BeanReflection;

/**
 * created by vkorovkin on 16.05.2018
 */
public class BaseValidationProcessor {

    private ValidationContext ctx;

    protected BaseValidationProcessor(ValidationContext ctx) {
        this.ctx = ctx;
    }

    protected Object getSource() {
        return getCtx().getCurNode().getSource();
    }


    public BeanReflection getSourceReflection() {
        return getCtx().getCurNode().getSourceReflection();
    }

    public BeanReflection getDestReflection() {
        return getCtx().getCurNode().getDestReflection();
    }

    protected ValidationContext getCtx() {
        return ctx;
    }
    
}
