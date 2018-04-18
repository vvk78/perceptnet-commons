/*
 * Copyright 2015 Perceptnet
 *
 * This source code is Perceptnet Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 *
 */

package com.perceptnet.commons.utils;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.*;

/**
 * @author Alexander Zadvinskiy
 */
public class ReflectionUtilsTest {

    private Method[] getters;
    private Method[] setters;
    private Method[] validGetters;
    private Method[] validSetters;

    @BeforeClass(groups = {UNIT})
    public void setup() {
        getters = GettersTest.class.getDeclaredMethods();
        setters = SettersTest.class.getDeclaredMethods();
        validGetters = ValidGettersTest.class.getDeclaredMethods();
        validSetters = ValidSettersTest.class.getDeclaredMethods();
    }

    @Test(groups = {UNIT}, expectedExceptions = IllegalArgumentException.class)
    public void testGetFieldNameException() {
        ReflectionUtils.getFieldName(getters[0]);
    }

    @Test(groups = {UNIT})
    public void testGetFieldName() {
        for (Method method : validGetters) {
            ReflectionUtils.getFieldName(method);
        }
    }

    @Test(groups = {UNIT})
    public void testIsGetterFalse() {
        assertFalse(ReflectionUtils.isGetter(null));
        for (Method method : setters) {
            assertFalse(ReflectionUtils.isGetter(method), "Unexpected getter: " + setters);
        }
        for (Method method : getters) {
            assertFalse(ReflectionUtils.isGetter(method), "Not a getter: " + method);
        }
    }

    @Test(groups = {UNIT})
    public void testIsGetterTrue() {
        for (Method method : validGetters) {
            assertTrue(ReflectionUtils.isGetter(method));
        }
    }

    @Test(groups = {UNIT})
    public void testIsSetterFalse() {
        assertFalse(ReflectionUtils.isSetter(null));
        for (Method method : getters) {
            assertFalse(ReflectionUtils.isSetter(method));
        }
        for (Method method : setters) {
            assertFalse(ReflectionUtils.isSetter(method));
        }
    }

    @Test(groups = {UNIT})
    public void testIsSetterTrue() {
        for (Method method : validSetters) {
            assertTrue(ReflectionUtils.isSetter(method), "Not a setter: " + method);
        }
    }

    @Test(groups = {UNIT})
    public void testGetCollectionItemTypeWhenSet() throws Exception {
        Method[] methods = CollectionGetter.class.getDeclaredMethods();
        Method getter = null;
        for (Method method : methods) {
            if (method.getName().equals("getItems")) {
               getter = method;
            }
        }
        assertNotNull(getter, "Collection getter is not found");
        Class childItemClass = ReflectionUtils.getCollectionItemClassFromGetter(getter);
        assertEquals(childItemClass, CollectionItem.class, "Wrong collection item class");
    }

    @Test(groups = {UNIT})
    public void testGetCollectionItemTypeWhenList() throws Exception {
        Method[] methods = ListGetter.class.getDeclaredMethods();
        Method getter = null;
        for (Method method : methods) {
            if (method.getName().equals("getItems")) {
                getter = method;
            }
        }
        assertNotNull(getter, "Collection getter is not found");
        Class childItemClass = ReflectionUtils.getCollectionItemClassFromGetter(getter);
        assertEquals(childItemClass, CollectionItem.class, "Wrong collection item class");
    }

    @Test(groups = {UNIT})
    public void testMapGetter() throws Exception {

    }


    class ValidGettersTest {

        private String a;
        private boolean b;
        private boolean c;

        public String getA() {
            return a;
        }

        public boolean isB() {
            return b;
        }

        public boolean getC() {
            return c;
        }
    }

    class ValidSettersTest {

        private String c;
        private boolean d;

        public void setC(String c) {
            this.c = c;
        }

        public void setD(boolean d) {
            this.d = d;
        }
    }

    class GettersTest {

        private String a;
        private String b;
        private boolean c;

        public String A() {
            return a;
        }

        public String B() {
            return b;
        }
    }

    class SettersTest {

        private String c;
        private String d;

        public void putC(String c) {
            this.c = c;
        }

        public void addD(String d) {
            this.d = d;
        }
    }

    private static class CollectionItem {

    }

    private static class ListGetter {
        private List<CollectionItem> items = new ArrayList<CollectionItem>();

        public List<CollectionItem> getItems() {
            return items;
        }
    }

    private static class CollectionGetter {
        private Set<CollectionItem> items = new HashSet<CollectionItem>();

        public Set<CollectionItem> getItems() {
            return items;
        }
    }
}
