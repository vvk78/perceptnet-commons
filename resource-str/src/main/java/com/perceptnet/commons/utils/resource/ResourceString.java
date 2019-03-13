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

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author VKorovkin
 */
public final class ResourceString implements Serializable, Comparable {

    private final String[] resourceKeys;
    private final String defaultValue;


    public ResourceString(String resourceKey) {
        this(resourceKey, "");
    }

    public ResourceString(String resourceKey, String defaultValue) {
        this(new String[]{resourceKey}, defaultValue);
    }

    public ResourceString(String primaryResourceKey, String secondaryResourceKey, String defaultValue) {
        this(new String[]{primaryResourceKey, secondaryResourceKey}, defaultValue);
    }

    public ResourceString(String[] resourceKeys, String defaultValue) {
        if (resourceKeys == null) {
            throw new NullPointerException("ResourceKeys is null");
        }
        if (resourceKeys.length == 0) {
            throw new NullPointerException("ResourceKeys is empty");
        }
        for (String resourceKey : resourceKeys) {
            if (resourceKey == null) {
                throw new NullPointerException("ResourceKey is null");
            }
        }
        this.resourceKeys = resourceKeys;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        ResourceManager rm = ResourceManagerProvider.get();
        for (int i = 0; i < resourceKeys.length; i++) {
            String resourceKey = resourceKeys[i];
            String value = rm.getResourceString(resourceKey);
            if (value != null) {
                return value;
            }
        }

        return defaultValue;
    }

    /**
     * synonym for toString(), just for shortness and convenience
     */
    public String s() {
        return toString();
    }

    public String getResourceKey() {
        return resourceKeys[0];
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        //auto generated method
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceString that = (ResourceString) o;

        return (Arrays.equals(resourceKeys, that.resourceKeys));
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }
        if (!(o instanceof ResourceString)) {
            return 1;
        }

        String otherStrVal = ((ResourceString) o).toString();

        String strVal = toString();

        if (otherStrVal == null && strVal == null) {
            return 0;
        } else if (strVal == null) {
            return -1;
        } else if (otherStrVal == null) {
            return 1;
        } else {
            return strVal.compareTo(otherStrVal);
        }
    }

    @Override
    public int hashCode() {
        //auto generated method
        return Arrays.hashCode(resourceKeys);
    }
}
