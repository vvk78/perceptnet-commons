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

/**
 * @author VKorovkin
 */
public final class ResourceString implements Serializable, Comparable {

    private final String resourceKey;
    private final String defaultValue;


    public ResourceString(String resourceKey) {
        this(resourceKey, "");
    }

    public ResourceString(String resourceKey, String defaultValue) {
        if (resourceKey == null) {
            throw new NullPointerException("resourceKey is null");
        }
        this.resourceKey = resourceKey;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        String value = ResourceManagerProvider.get().getResourceString(resourceKey);
        return value != null ? value : defaultValue;
    }

    /**
     * synonym for toString(), just for shortness and convenience
     */
    public String s() {
        return toString();
    }

    public String getResourceKey() {
        return resourceKey;
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

        return (resourceKey.equals(that.resourceKey));
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
        return resourceKey.hashCode();
    }
}
