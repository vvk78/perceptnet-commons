package com.perceptnet.commons.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * created by vkorovkin on 18.06.2018
 */
public class IoUtils {
    public static void closeSafely(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
