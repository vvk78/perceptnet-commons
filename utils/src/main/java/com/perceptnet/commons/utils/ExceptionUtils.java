package com.perceptnet.commons.utils;

/**
 * Created by VKorovkin on 03.04.2015.
 */
public class ExceptionUtils {

    public static <T extends Throwable> T getTopmostCause(Throwable source, Class<T> soughtThrowableClass) {
        if (soughtThrowableClass == null) {
            throw new NullPointerException("soughtThrowableClass is null");
        }
        if (source == null) {
            return null;
        }
        if (soughtThrowableClass.isAssignableFrom(source.getClass())) {
            return (T) source;
        } else if (source.getCause() != null) {
            //Attention! recursion:
            return getTopmostCause(source.getCause(), soughtThrowableClass);
        } else {
            return null;
        }
    }

    public static <T extends Throwable> T getDeepestCause(Throwable source, Class<T> soughtThrowableClass) {
        if (soughtThrowableClass == null) {
            throw new NullPointerException("soughtThrowableClass is null");
        }
        if (source == null) {
            return null;
        }
        if (source.getCause() != null) {
            //Attention! recursion:
            T result = getDeepestCause(source.getCause(), soughtThrowableClass);
            if (result != null) {
                return result;
            }
        }
        if (soughtThrowableClass.isAssignableFrom(source.getClass())) {
            return (T) source;
        } else {
            return null;
        }
    }

}
