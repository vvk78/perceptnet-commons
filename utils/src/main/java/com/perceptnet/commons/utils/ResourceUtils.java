package com.perceptnet.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getResourceLines(String resourceName) {
        return getResourceLines(resourceName, false, false, null, -1);
    }

    public static List<String> getResourceLines(String resourceName, boolean trim, boolean skipEmpty, String skipPrefix,
                                                int linesNumApproximation) {
        InputStream is = resource(resourceName);
        BufferedReader reader = null;
        try {
            try {
                reader = new BufferedReader(new InputStreamReader(is));
                List<String> result = new ArrayList<>(linesNumApproximation <= 0 ? 10 : linesNumApproximation);
                String curLine;
                while ((curLine = reader.readLine()) != null) {
                    if (trim) {
                        curLine = curLine.trim();
                    }
                    if (skipEmpty && curLine.isEmpty()) {
                        continue;
                    }
                    if (skipPrefix != null && curLine.startsWith(skipPrefix)) {
                        continue;
                    }
                    result.add(curLine);
                }
                return result;
            } catch (IOException e) {
                throw new RuntimeException("" + e + " while reading content of resource '" + resourceName + "'");
            }
        } finally {
            IoUtils.closeSafely(is);
            IoUtils.closeSafely(reader);
        }
    }



}
