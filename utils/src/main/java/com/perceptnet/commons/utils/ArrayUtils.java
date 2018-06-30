package com.perceptnet.commons.utils;

/**
 * created by vkorovkin on 19.06.2018
 */
public class ArrayUtils {
//    public static <T> Iterable<T> combine(T[] a1, T[] a2) {
//        return new
//    }

    public static void copyTo(byte[] source, byte dest[], int destOffset) {
        if (dest.length < destOffset + source.length) {
            throw new IndexOutOfBoundsException("Bad arguments of copyTo method: " + source.length + ", " + dest.length + ", " + destOffset);
        }
        for (int i = 0; i < source.length; i++) {
            dest[destOffset++] = source[i];
        }
    }
}
