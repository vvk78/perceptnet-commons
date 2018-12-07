package com.perceptnet.commons.utils;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 12/7/2018
 */
public class ResourceUtils {

    public static boolean exists(String resName) {
        return ResourceUtils.class.getClassLoader().getResource(resName) != null;
    }



}
