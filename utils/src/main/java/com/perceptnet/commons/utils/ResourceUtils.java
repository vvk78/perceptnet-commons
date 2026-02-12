package com.perceptnet.commons.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 12/7/2018
 */
public class ResourceUtils {

    public static boolean exists(String resName) {
        return ResourceUtils.class.getClassLoader().getResource(resName) != null;
    }

    public static String obtainResourceAsText(String resName) {
        InputStream in = ResourceUtils.class.getClassLoader().getResourceAsStream(resName);
        if (in == null) {
            throw new IllegalStateException("No resource '" + resName + "' found, make sure it is available on CLASSPATH");
        }
        try {
            String resText = IoUtils.readStreamAsText(in, -1, StandardCharsets.UTF_8.name());
            return resText;
        } finally {
            IoUtils.closeSafely(in);
        }
    }

    public static String obtainResourceAsNixText(String resName) {
        String result = obtainResourceAsText(resName);
        result = result.replace("\r\n", "\n");
        return result;
    }


}
