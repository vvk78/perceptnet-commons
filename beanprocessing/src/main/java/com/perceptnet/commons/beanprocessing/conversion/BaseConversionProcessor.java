/*
 * Copyright 2017 Perceptnet
 *
 * This source code is Perceptnet Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 *
 */

package com.perceptnet.commons.beanprocessing.conversion;

import com.perceptnet.commons.reflection.BeanReflection;

/**
 * Created by vkorovkin on 20.04.2015.
 */
class BaseConversionProcessor {

    private ConversionContext ctx;

    protected BaseConversionProcessor(ConversionContext ctx) {
        this.ctx = ctx;
    }

    protected Object getSource() {
        return getCtx().getCurNode().getSource();
    }

    protected Object getDest() {
        return getCtx().getCurNode().getDest();
    }

    public BeanReflection getSourceReflection() {
        return getCtx().getCurNode().getSourceReflection();
    }

    public BeanReflection getDestReflection() {
        return getCtx().getCurNode().getDestReflection();
    }

    protected ConversionContext getCtx() {
        return ctx;
    }

}
