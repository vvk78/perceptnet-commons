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
package com.perceptnet.commons.utils.resource;


import com.perceptnet.commons.utils.ClassUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author VKorovkin
 */
public class ResourceManagerProvider {
    private static final AtomicReference<ResourceManager> instanceRef = new AtomicReference<>(createManager());

    public static ResourceManager get() {
        return instanceRef.get();
    }

    public static void set(ResourceManager manager) {
        instanceRef.set(manager);
    }

    private static ResourceManager createManager() {
        String factoryClassName = System.getProperty("com.perceptnet.commons.utils.resource.ResourceManagerFactory");
        if (factoryClassName != null) {
            ResourceManagerFactory factory = (ResourceManagerFactory) ClassUtils.createSafely(factoryClassName);
            if (factory != null) {
                try {
                    return factory.createResourceManager();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //Dummy manager:
        return new ResourceManager() {
            @Override
            public String getResourceString(String resourceKey) {
                return null;
            }
        };
    }


}
