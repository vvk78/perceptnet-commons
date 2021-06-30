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

package com.perceptnet.commons.reflection;

import com.perceptnet.commons.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by VKorovkin on 16.03.2015.
 */
public class FieldReflection {
    private static final Object[] EMPTY_ARGS = new Object[0];


    public enum Kind {
        FLAT,       //field processed as is
        COLLECTION, //collection fields may be processed recursively for every item
        REFERENCE,  //nested fields are processed recursively
        // ---- special flat field processed specially
        ID,
        VERSION
    }

    private final String fieldName;
    private final Class fieldType;
    private final Class declaringClass;
    /**
     * Only for collection fields -- collection item type
     */
    private Class collectionItemClass;
    private boolean isCollectionItemClassFlat;
    private Method getter;
    private Method setter;
    private Kind fieldKind;
    private boolean finalized;

    private Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
    private Map extendedAttributes = new ConcurrentHashMap<>(2);


    public static FieldReflection createFromGetter(String fieldName, Method getter) {
        return new FieldReflection(fieldName, getter.getReturnType(), getter.getDeclaringClass(), getter, null);
    }

    public static FieldReflection createFromSetter(String fieldName, Method setter) {
        return new FieldReflection(fieldName, setter.getParameterTypes()[0], setter.getDeclaringClass(), null, setter);
    }

    private FieldReflection(String fieldName, Class fieldType, Class declaringClass, Method getter, Method setter) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is null");
        }
        if (getter == null && setter == null) {
            throw new IllegalArgumentException("Both getter and setter cannot be nulls");
        }
        if (fieldType == null) {
            throw new NullPointerException("fieldType is null");
        }
        if (declaringClass == null) {
            throw new NullPointerException("declaringClass is null");
        }
        this.declaringClass = declaringClass;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.getter = getter;
        this.setter = setter;
        detectCollectionItemTypeIfNeeded();
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return annotations.containsKey(annotationClass);
    }

    public <A extends Annotation> A getAnnotation(Class<? extends Annotation> annotationClass) {
        return (A) annotations.get(annotationClass);
    }

    void putAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        annotations.put(annotationClass, annotation);
    }

    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return annotations;
    }

    public Method getGetter() {
        return getter;
    }

    void setGetter(Method getter) {
        this.getter = getter;
        detectCollectionItemTypeIfNeeded();
    }

    private void detectCollectionItemTypeIfNeeded() {
        if (this.getter != null && Collection.class.isAssignableFrom(fieldType)) {
            try {
                collectionItemClass = ReflectionUtils.getCollectionItemClassFromGetter(this.getter);
            } catch (RuntimeException e) {
                throw new RuntimeException("Cannot detect collection type for field " +
                        getDeclaringClass().getSimpleName() + "." + getFieldName() + " due to: " + e, e);
            }
        }
    }

    public Method getSetter() {
        return setter;
    }

    void setSetter(Method setter) {
        this.setter = setter;
    }

    public void setValue(Object onObj, Object value) {
        if (isReadOnly()) {
//            throw new IllegalStateException("Cannot set value on read-only field " + toString());
        } else {
            try {
                setter.invoke(onObj, value);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Cannot set value '" + value + "' on " + toString() +
                        " due to " + e, e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Cannot set value '" + value + "' on " + toString() +
                        " due to " + e.getTargetException(), e.getTargetException());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot set value '" + value + "' on " + toString() +
                        " due to " + e, e);
            }
        }
    }

    public Object getValue(Object onObj) {
        if (isWriteOnly()) {
            throw new IllegalStateException("Cannot get value of write-only field " + toString());
        }
        try {
            return getter.invoke(onObj, EMPTY_ARGS);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Cannot get value on " + toString() +
                    " due to " + e.getTargetException(), e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot get value on " + toString() + " due to " + e, e);
        }
    }

    private void transferValue(Object fromObj, Object toObj) {
        setValue(toObj, getValue(fromObj));
    }

    public Class getFieldType() {
        return fieldType;
    }

    public Kind getFieldKind() {
        return fieldKind;
    }

    void setFieldKind(Kind fieldKind) {
        this.fieldKind = fieldKind;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isReadOnly() {
        return setter == null;
    }

    public boolean isWriteOnly() {
        return getter == null;
    }

    public boolean isEnum() {
        return fieldType.isEnum();
    }

    public Class getCollectionItemClass() {
        return collectionItemClass;
    }

    public boolean isCollectionItemClassFlat() {
        return isCollectionItemClassFlat;
    }

    void setCollectionItemClassFlat(boolean collectionItemClassFlat) {
        isCollectionItemClassFlat = collectionItemClassFlat;
    }

    void setCollectionItemClass(Class collectionItemClass) {
        this.collectionItemClass = collectionItemClass;
    }

    public Class getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public String toString() {
        return declaringClass.getSimpleName() + "." + fieldName;
    }

    public Map getExtendedAttributes() {
        return extendedAttributes;
    }

    void finalizeAfterBuild() {
        finalized = true;
        annotations = annotations.isEmpty() ? Collections.EMPTY_MAP : Collections.unmodifiableMap(annotations);
        //extendedAttributes = extendedAttributes.isEmpty() ? Collections.EMPTY_MAP : Collections.unmodifiableMap(extendedAttributes);
    }

    void assertNotFinalized() {
        if (finalized) {
            throw new IllegalStateException("Field reflection " + this + " is finalized");
        }
    }
}
