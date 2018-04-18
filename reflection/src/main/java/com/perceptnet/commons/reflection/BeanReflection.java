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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vkorovkin on 16.03.15.
 */
public class BeanReflection {

    private boolean isFinalized = false;


    private final Class beanClass;

    private FieldReflection idField;
    private FieldReflection versionField;

    private Map<String, FieldReflection> allFields = new HashMap<>();

    private Map<String, FieldReflection> flatFields = new HashMap<>();
    private Map<String, FieldReflection> collections = new HashMap<>();
    private Map<String, FieldReflection> references = new HashMap<>();

    public BeanReflection(Class beanClass) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass is null");
        }
        this.beanClass = beanClass;
    }

    public Map<String, FieldReflection> getFlatFields() {
        return flatFields;
    }

    public Map<String, FieldReflection> getCollections() {
        return collections;
    }

    public Map<String, FieldReflection> getReferences() {
        return references;
    }

    public FieldReflection getField(String fieldName) {
        return allFields.get(fieldName);
    }

    public FieldReflection obtainField(String fieldName) {
        FieldReflection result = allFields.get(fieldName);
        if (result == null) {
            throw new IllegalStateException("No '" + fieldName + "' found in " + beanClass.getName());
        }
        return result;
    }

    public Collection<FieldReflection> getAllFields() {
        return allFields.values();
    }

    void registerField(String fieldName, FieldReflection field) {
        assert fieldName != null : "fieldName is null";
        assert fieldName.equals(field.getFieldName())
                : "fieldName for registration differs from name of field (" +
                                fieldName + " and " + field.getFieldName();
        assertNotFinalized();

        if (allFields.put(fieldName, field) != null) {
            throw new IllegalStateException("Double field registration: " + fieldName + " for bean " + beanClass.getName());
        }

        switch (field.getFieldKind()) {
            case COLLECTION:
                collections.put(fieldName, field);
                break;

            case REFERENCE:
                references.put(fieldName, field);
                break;

            case FLAT:
                flatFields.put(fieldName, field);
                break;

            case ID:
                if (idField != null) {
                    throw new IllegalStateException("Id field is defined already");
                }
                idField = field;
                break;

            case VERSION:
                if (versionField != null) {
                    throw new IllegalStateException("Version field is defined already");
                }
                versionField = field;
                break;

            default: throw new IllegalStateException("Unsupported field kind: " + field.getFieldKind());
        }
    }

    private void assertNotFinalized() {
        if (isFinalized) {
            throw new IllegalStateException("Bean reflection for class " + beanClass + " is finalized, cannot modify");
        }
    }

    public FieldReflection getIdField() {
        return idField;
    }

    public FieldReflection getVersionField() {
        return versionField;
    }

    /**
     * To be called when bean reflection is fully built
     */
    void finalizeAfterBuild() {
        assertNotFinalized();
        this.allFields = Collections.unmodifiableMap(allFields);
        this.flatFields = Collections.unmodifiableMap(flatFields);
        this.references = Collections.unmodifiableMap(references);
        this.collections = Collections.unmodifiableMap(collections);
        this.isFinalized = true;
    }

    public Class getBeanClass() {
        return beanClass;
    }
}
