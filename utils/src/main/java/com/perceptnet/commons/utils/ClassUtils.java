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

package com.perceptnet.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collection;

/**
 * Created by vkorovkin on 17.03.15.
 */
public class ClassUtils {

    private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

    public static String simpleName(String qualifiedName) {
        int idx = qualifiedName.lastIndexOf(".");
        if (idx == -1) {
            return qualifiedName;
        }
        return qualifiedName.substring(idx + 1);
    }

    public static Class classForNameUnsafely(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("" + e, e);
        }
    }

    public static Class classForNameSafely(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            if (log.isTraceEnabled()) {
                log.trace("No class " + className, e);
            }
            return null;
        }
    }

    /**
     * Returns true if given class is assignable from any one of the passed classes.
     * @return
     */
    public static boolean isAssignableFromAny(Class checkedClass, Collection<Class> classes) {
        for (Class aClass : classes) {
            if (checkedClass.isAssignableFrom(aClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if given class is assignable from any one of the passed checkedClasses.
     * @return
     */
    public static boolean isAnyAssignableFrom(Collection<Class> checkedClasses, Class clazz) {
        for (Class checkedClass : checkedClasses) {
            if (checkedClass.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T createUnsafely(Class<T> aClass) {
        try {
            return aClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create '" + aClass.getName() + "' due to " + e, e);
        }
    }

    public static <T> T createSafely(Class<T> aClass) {
        try {
            return aClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T createUnsafely(String className, Object ... args) {
        try {
            Class[] argTypes = extractTypes(args);
            Class<T> aClass = (Class<T>) Class.forName(className);
            Constructor<T> constructor = aClass.getConstructor(argTypes);
            if (constructor != null) {
                return constructor.newInstance(args);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot create: " + className + " due to: " + e);
        }
    }

    public static <T> T createUnsafely(Class<T> clazz, Object ... args) {
        return createUnsafely(clazz, extractTypes(args), args);
    }

    public static <T> T createUnsafely(Class<T> clazz, Class[] argTypes, Object ... args) {
        try {
            Constructor<T> constructor = clazz.getConstructor(argTypes);
            if (constructor != null) {
                return constructor.newInstance(args);
            } else {
                throw new RuntimeException("Constructor is not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot create: " + clazz.getName() + " due to: " + e);
        }
    }

    public static <T> T createSafely(Class<T> aClass, Object ... args) {
        try {
            Class[] argTypes = extractTypes(args);
            Constructor<T> constructor = aClass.getConstructor(argTypes);
            if (constructor != null) {
                return constructor.newInstance(args);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <E > E createExceptionSafely(Class<E> aClass, String msg, Throwable cause) {
        try {
            Constructor<E> constructor = aClass.getConstructor(String.class, Throwable.class);
            if (constructor != null) {
                return constructor.newInstance(msg, cause);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <E extends Exception> E createExceptionSafely(Class<E> aClass, String msg) {
        try {
            Constructor<E> constructor = aClass.getConstructor(String.class);
            if (constructor != null) {
                return constructor.newInstance(msg);
            } else {
                return aClass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object createSafely(String fullClassName) {
        try {
            Class<?> clazz = Class.forName(fullClassName);
            return createSafely(clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object createUnsafely(String fullClassName) {
        try {
            Class<?> clazz = Class.forName(fullClassName);
            return createUnsafely(clazz);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create '" + fullClassName + "' due to " + e, e);
        }
    }

    private static Class[] extractTypes(Object ... args) {
        Class[] result = new Class[args.length];
        int i = 0;
        for (Object arg : args) {
            if (arg == null) {
                throw new IllegalStateException("Arg [" + i + "] is null. Cannot extract types");
            }
            result[i++] = arg.getClass();
        }
        return result;
    }

}
