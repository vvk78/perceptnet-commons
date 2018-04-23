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

import com.perceptnet.commons.utils.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.stream.Collectors;


/**
 * @author VKorovkin
 */
public class ResourceMessage implements Serializable {
    private final ResourceString format;
    private final Object[] params;
    public final static String SHORT_SUFFIX = "short";

    public ResourceMessage(String resourceKey) {
        this(new ResourceString(resourceKey, null), null);
    }

    public ResourceMessage(ResourceString format) {
        this(format, null);
    }

    public ResourceMessage(ResourceString format, Object... params) {
        if (format == null) {
            throw new NullPointerException("format is null");
        }
        this.format = format;
        this.params = params;
    }

    public ResourceString getFormat() {
        return format;
    }

    @Override
    public String toString() {
        if (params == null || params.length == 0) {
            return "" + format;
        } else {
            try {
                return String.format("" + format, params);
            } catch (MissingFormatArgumentException e) {
                throw new RuntimeException("Cannot format '" + format + "' due to " + e, e);
            }
        }
    }

    public static ResourceMessage withStaticString(String message) {
        return new ResourceMessage(new ResourceString("", message));
    }

    public static String message(String key) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        return new ResourceMessage(key).toString();
    }

    public static String message(String  key, Object ... args){
        if (StringUtils.isBlank(key)){
            return "";
        }
        return new ResourceMessage(new ResourceString(key), args).toString();
    }



    public static String message(Enum e) {
        return message(e, null);
    }

    public static String shortMessage(Enum e) {
        return message(e, SHORT_SUFFIX);
    }

    public static String message(Enum e, String detail) {
        if (e == null) {
            return "";
        }
        if (StringUtils.isBlank(detail)) {
            return new ResourceMessage(e.getClass().getCanonicalName() + "." + e.name()).toString();
        }
        return new ResourceMessage(e.getClass().getCanonicalName() + "." + e.name() + "." + detail).toString();
    }

    public static String message(Enum e, String detail, Object... args) {
        if (e == null) {
            return "";
        }
        if (StringUtils.isBlank(detail)) {
            return new ResourceMessage(new ResourceString(e.getClass().getCanonicalName() + "." + e.name()), args).toString();
        }
        return new ResourceMessage(new ResourceString(e.getClass().getCanonicalName() + "." + e.name() + "." + detail), args).toString();
    }
}
