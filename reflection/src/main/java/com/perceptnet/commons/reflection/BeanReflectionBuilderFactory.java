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

package com.perceptnet.commons.reflection;

/**
 * Created by vkorovkin on 19.03.15.
 */
public interface BeanReflectionBuilderFactory {
    /**
     * Creates a special reflection builder for this class or null if default builder may be used.
     *
     * @throws UnsupportedBeanClassException if bean class is illegal and
     *          its reflection should not be created and cached in provider
     */
    BeanReflectionBuilder createBuilderForClass(Class beanClass) throws UnsupportedBeanClassException;
}
