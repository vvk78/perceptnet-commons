package com.perceptnet.commons.utils;

import java.io.InputStream;

/**
 * To get resource stream both on Android and as usual
 * created by vkorovkin (vkorovkin@gmail.com) on 04.10.2019
 */
public class ResourceResolver {
    private static final ResourceResolver INSTANCE = instantiate();

    public static InputStream getResourceAsStream(String name) {
        return INSTANCE.resourceAsStream(name);
    }

    protected InputStream resourceAsStream(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }

    private static ResourceResolver instantiate() {
        //ResourceResolver result = (ResourceResolver) ClassUtils.createSafely("com.aim.aimper.android.AndroidResourceResolverImpl");
        //return result != null ? result : new ResourceResolver();
        return new ResourceResolver();
    }


}
