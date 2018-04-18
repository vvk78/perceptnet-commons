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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by vkorovkin on 16.03.15.
 */
public class BeanReflectionProviderCachingImpl implements ReflectionProvider {
    private static final Logger log = LoggerFactory.getLogger(BeanReflectionProviderCachingImpl.class);

    private AtomicReference<BeanReflectionBuilderFactory> builderFactory = new AtomicReference<>();
    private final ConcurrentMap<Class, BeanReflection> cache = new ConcurrentHashMap<>();


    /**
     * Returns bean reflection for a bean class. If given bean class
     * @param clazz
     * @return
     */
    public BeanReflection getReflection(Class clazz) {
        BeanReflection result = cache.get(clazz);
        if (result == null) {
            BeanReflectionBuilder builder = obtainBuilderForBeanClass(clazz);
            if (builder == null) {
                log.warn("Bean {} is not supported by given reflection provider");
                return null;
            }

            result = builder.build(clazz);
            BeanReflection current = cache.putIfAbsent(clazz, result);
            result = current != null ? current : result;
        }
        return result;
    }

    /**
     * Returns proper reflection builder for given bean class.
     *
     */
    private BeanReflectionBuilder obtainBuilderForBeanClass(Class clazz) {
        BeanReflectionBuilder builder = null;
        BeanReflectionBuilderFactory builderFactory = this.builderFactory.get();

        if (builderFactory != null) {
            try {
                builder = builderFactory.createBuilderForClass(clazz);
            } catch (UnsupportedBeanClassException e) {
                if (log.isDebugEnabled()) {
                    //if debug level is set, use warn level still, but add more details
                    log.warn("Bean class {} is forbidden to create a reflection for", clazz, e);
                } else {
                    log.warn("Bean class {} is forbidden to create a reflection for", clazz);
                }
                return null;
            }
        }

        if (builder == null) {
            builder = new BeanReflectionBuilder();
        }
        tuneBuilderForClass(builder, clazz);

        return builder;
    }

    protected void tuneBuilderForClass(BeanReflectionBuilder builder, Class forClass) {
    }

    public BeanReflectionProviderCachingImpl setBuilderFactory(BeanReflectionBuilderFactory builderFactory) {
        log.info("Bean builder factory {} set in bean reflection provider", builderFactory != null ? builderFactory.getClass() : null);
        this.builderFactory.set(builderFactory);
        return this;
    }
}
