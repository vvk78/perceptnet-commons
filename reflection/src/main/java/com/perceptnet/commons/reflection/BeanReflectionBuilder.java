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

import com.perceptnet.commons.utils.ClassUtils;
import com.perceptnet.commons.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by vkorovkin on 16.03.15.
 */
public class BeanReflectionBuilder {
    private static final Set<Class> BASIC_FLAT_FIELD_TYPES =
            new HashSet<Class>(Arrays.asList(String.class, Boolean.class, Number.class, Date.class, Enum.class));

    private static final Logger log = LoggerFactory.getLogger(BeanReflectionBuilder.class);

    private boolean processSetters = true;
    private boolean processGetters = true;
    private BeanReflection beanReflection;
    private Set<Class<? extends Annotation>> selectedAnnotations;

    private Set<Class> specialFlatFieldTypes;
    private ReflectionBuilderMethodFilter specialMethodFilter;


    public BeanReflection build(Class beanClass) {
        if (beanReflection != null) {
            throw new IllegalStateException("Bean reflection builder has been used already.");
        }
        boolean skipInterfaceMethods = !beanClass.isInterface();
        beanReflection = new BeanReflection(beanClass);

        Method[] methods = beanClass.getMethods();
        for (Method method : methods) {
            //isSynthetic() check added because
            // there are two methods getId() on BaseDo - one returns Object another Long.
            //Must somehow be related with generic interface Identified implementation?
            if (method.isSynthetic()
                    || method.getDeclaringClass().equals(Object.class)
                    || (skipInterfaceMethods && method.getDeclaringClass().isInterface())
                    || method.getDeclaringClass().isAnnotation()) {
                continue; //do not process methods inherited from object, declared in interfaces or in annotations
            }

            if (specialMethodFilter != null && !specialMethodFilter.isMethodToBeProcessed(method)) {
                continue;
            }

            if (processAsGetter(method)) {
                log.trace("Method {} processed as getter", method);
                continue;
            }

            if (processAsSetter(method)) {
                log.trace("Method {} processed as setter", method);
                continue;
            }
        }

        extractAnnotationsFields(beanClass);

        //finalize fields and whole bean reflection:
        for (FieldReflection fieldReflection : beanReflection.getAllFields()) {
            tuneFieldReflectionBeforeFinalization(fieldReflection);
        }
        tuneBeanReflectionBeforeFinalization(beanReflection);
        beanReflection.finalizeAfterBuild();

        return beanReflection;
    }

    private void extractAnnotationsFields(Class beanClass) {
        //first assemble fields from hierarchy
        Map<String, Field> fieldByNames = new HashMap<String, Field>();
        assembleFieldsFromDeclaredFields(beanClass, fieldByNames);

        for (Field field : fieldByNames.values()) {
            FieldReflection fieldReflection = beanReflection.getField(field.getName());
            if (fieldReflection == null) {
                continue;
            }

            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (isSelectedAnnotation(annotation)) {
                    fieldReflection.putAnnotation(annotation);
                }
            }
        }
    }

    private void assembleFieldsFromDeclaredFields(Class clazz, Map<String, Field> fieldByNames) {
        if (clazz.equals(Object.class)) {
            return;
        }
        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            //attention recursion:
            assembleFieldsFromDeclaredFields(clazz.getSuperclass(), fieldByNames);
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (beanReflection.getField(field.getName()) != null) {
                fieldByNames.put(field.getName(), field);
            }
        }
    }

    private boolean processAsGetter(Method method) {
        if (ReflectionUtils.isGetter(method)) {
            String fieldName = ReflectionUtils.getFieldName(method);
            FieldReflection field = beanReflection.getField(fieldName);
            if (field == null) {
                field = tuneFieldAfterCreation(FieldReflection.createFromGetter(fieldName, method));
                beanReflection.registerField(fieldName, field);
            } else {
                field.setGetter(method);
            }
            return true;
        }

        return false;
    }

    private boolean processAsSetter(Method method) {
        if (ReflectionUtils.isSetter(method)) {
            String fieldName = ReflectionUtils.getFieldName(method);
            FieldReflection field = beanReflection.getField(fieldName);
            if (field == null) {
                field = tuneFieldAfterCreation(FieldReflection.createFromSetter(fieldName, method));
                beanReflection.registerField(fieldName, field);
            }

            field.setSetter(method);
            return true;
        }

        return false;
    }

    /**
     * Additionally tunes field right after its creation.
     */
    private FieldReflection tuneFieldAfterCreation(FieldReflection fieldReflection) {
        if (detectIfIsSpecial(fieldReflection)) {
            return fieldReflection;
        }

        Class fieldType = fieldReflection.getFieldType();
        if (ReflectionUtils.isCollection(fieldType)) {
            fieldReflection.setFieldKind(FieldReflection.Kind.COLLECTION);
            if (fieldReflection.getCollectionItemClass() != null) {
                fieldReflection.setCollectionItemClassFlat(isFlatType(fieldReflection.getCollectionItemClass()));
            }
        } else if (isFlatType(fieldType)) {
            fieldReflection.setFieldKind(FieldReflection.Kind.FLAT);
        } else {
            fieldReflection.setFieldKind(FieldReflection.Kind.REFERENCE);
        }
        return fieldReflection;
    }

    protected void tuneBeanReflectionBeforeFinalization(BeanReflection reflection) {
        //to be defined in descendants if needed
    }

    protected void tuneFieldReflectionBeforeFinalization(FieldReflection reflection) {
        //to be defined in descendants if needed
    }

    public boolean isFlatType(Class fieldType) {
        return ReflectionUtils.isPrimitive(fieldType)
                || ClassUtils.isAnyAssignableFrom(BASIC_FLAT_FIELD_TYPES, fieldType)
                || specialFlatFieldTypes != null && ClassUtils.isAnyAssignableFrom(specialFlatFieldTypes, fieldType);
    }

    private boolean detectIfIsSpecial(FieldReflection fieldReflection) {
        // todo consider using Identified and Versioned interfaces from "abstractions" module
        if (fieldReflection.getFieldName().equals("id")) {
            fieldReflection.setFieldKind(FieldReflection.Kind.ID);
            return true;
        } else if (
                (fieldReflection.getFieldType().equals(Integer.class) || fieldReflection.getFieldType().equals(int.class))
                        && fieldReflection.getFieldName().equals("version")) {
            fieldReflection.setFieldKind(FieldReflection.Kind.VERSION);
            return true;
        } else {
            return false;
        }
    }

    private boolean isSelectedAnnotation(Annotation annotation) {
        return selectedAnnotations == null || selectedAnnotations.contains(annotation.annotationType());
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // m e t h o d s     f o r      t u n i n g

    public BeanReflectionBuilder setSelectedAnnotations(Class<? extends Annotation>... annotations) {
        if (selectedAnnotations == null) {
            selectedAnnotations = new HashSet<>();
        }
        selectedAnnotations.addAll(Arrays.asList(annotations));
        return this;
    }

    public BeanReflectionBuilder setProcessSetters(boolean processSetters) {
        this.processSetters = processSetters;
        return this;
    }

    public BeanReflectionBuilder setProcessGetters(boolean processGetters) {
        this.processGetters = processGetters;
        return this;
    }

    public BeanReflectionBuilder addSpecialFlatTypes(Class ... classes) {
        if (this.specialFlatFieldTypes == null) {
            this.specialFlatFieldTypes = new HashSet<>(Arrays.asList(classes));
        } else {
            this.specialFlatFieldTypes.addAll(Arrays.asList(classes));
        }
        return this;
    }

    public BeanReflectionBuilder setSpecialMethodFilter(ReflectionBuilderMethodFilter specialMethodFilter) {
        this.specialMethodFilter = specialMethodFilter;
        return this;
    }
}
