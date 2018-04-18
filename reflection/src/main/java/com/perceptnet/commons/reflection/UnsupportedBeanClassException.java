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
 * This exception is thrown when a reflection is asked for unsupported bean class
 *
 * Created by vkorovkin on 19.03.15.
 */
public class UnsupportedBeanClassException extends Exception {
    private final Class badBeanClass;

    public UnsupportedBeanClassException(Class badBeanClass) {
        this("Unsupported bean class: " + badBeanClass, badBeanClass);
    }

    public UnsupportedBeanClassException(String message, Class badBeanClass) {
        super(message);
        this.badBeanClass = badBeanClass;
    }
}
