package com.perceptnet.commons.utils;

import java.io.InputStream;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 12/7/2018
 */
public class ResourceUtils {

    public static boolean exists(String resName) {
        return ResourceUtils.class.getClassLoader().getResource(resName) != null;
    }

    public static String resourceText(String resourceName) {
        return IoUtils.readStreamAsText(resource(resourceName), 0, null);
    }

    public static InputStream resource(String resourceName) {
        if (resourceName == null) {
            throw new NullPointerException("ResourceName is null");
        }
        InputStream result = ResourceResolver.getResourceAsStream(resourceName);
        if (result == null) {
            throw new RuntimeException("Resource " + resourceName + " is not found. Check classpath.");
        }
        return result;
    }



}
