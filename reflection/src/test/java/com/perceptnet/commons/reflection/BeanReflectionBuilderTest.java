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

import com.perceptnet.commons.utils.Joiner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.perceptnet.commons.reflection.FieldReflection.*;

import static com.perceptnet.commons.tests.TestGroups.*;
import static org.testng.Assert.*;

public class BeanReflectionBuilderTest {
    private BeanReflectionBuilder builder;
    private BeanReflection testBeanReflection;

    @BeforeMethod(groups = {UNIT, INTEGRATION, FUNCTIONAL, DEBUG, TEMPORARILY_DISABLED})
    public void setUp() throws Exception {
        builder = new BeanReflectionBuilder();
        testBeanReflection = builder.build(TestBean.class);

    }

    @Test(groups = {UNIT})
    public void testFieldRegistration() throws Exception {
        //flat
        assertFieldParams("name", true, true, Kind.FLAT, String.class, null);
        assertFieldParams("createdOn", true, true, Kind.FLAT, Date.class, null);
        assertFieldParams("modifiedAt", true, true, Kind.FLAT, Timestamp.class, null);
        assertFieldParams("effectiveFrom", true, true, Kind.FLAT, java.sql.Date.class, null);
        assertFieldParams("readOnlyCode", true, false, Kind.FLAT, Integer.class, null);
        assertFieldParams("writeOnlyPassword", false, true, Kind.FLAT, String.class, null);
        assertFieldParams("weight", true, true, Kind.FLAT, Double.class, null);

        //special
        assertFieldParams("id", true, true, Kind.ID, Long.class, null);
        assertFieldParams("version", true, true, Kind.VERSION, Integer.class, null);

        //references
        assertFieldParams("parent", true, true, Kind.REFERENCE, Parent.class, null);
        assertFieldParams("readOnlyParent", true, false, Kind.REFERENCE, Parent.class, null);

        //collections:
        assertCollectionFieldParams("items", true, false, Set.class, CollectionItemB.class, null);
        assertCollectionFieldParams("listItems", true, true, List.class, CollectionItemA.class, null);
    }

    @Test(groups = {UNIT})
    public void testTextSearchParams() throws Exception {
        builder = new BeanReflectionBuilder();
        BeanReflection r = builder.build(TextSearchParams.class);
        FieldReflection f = r.getField("authorName");
        assertNotNull(f, "authorName field is null");
        assertFalse(f.isReadOnly(), "authorName field is readOnly");
    }

    @Test(groups = {UNIT})
    public void testTextSearchParamsForGrabbing() throws Exception {
        builder = new BeanReflectionBuilder();
        BeanReflection r = builder.build(TextSearchParamsForGrabbing.class);
        FieldReflection f = r.getField("authorName");
        assertNotNull(f, "authorName field is null");
        assertFalse(f.isReadOnly(), "authorName field is readOnly");
    }

    @Test(groups = {UNIT})
    public void testFlatCollection() throws Exception {
        FieldReflection fr = testBeanReflection.getCollections().get("lines");
        assertEquals(String.class, fr.getCollectionItemClass(), "Not expected item type for 'lines' collection.");
        assertTrue(fr.isCollectionItemClassFlat());
    }

    private void assertFieldParams(String name, boolean hasGetter, boolean hasSetter,
                                   Kind fieldKind, Class fieldType,
                                   Class[] hasAnnotations) {
        FieldReflection field = testBeanReflection.getField(name);
        assertNotNull(field, "Field '" + name + "' is not found");
        assertEquals(field.getFieldKind(), fieldKind, "Wrong field kind for " + name);
        assertEquals(field.getFieldType(), fieldType, "Wrong field type for " +  name);
        assertEquals(field.getGetter() != null, hasGetter, "Wrong read access for " +  name);
        assertEquals(field.getSetter() != null, hasSetter, "Wrong write access for " +  name);

        //check field is specially accessible according to its type:
        if (fieldKind == Kind.FLAT) {
            assertEquals(testBeanReflection.getFlatFields().get(name), field, "Field '" +
                                                            name + "' is not specially accessible ");
            assertNull(testBeanReflection.getReferences().get(name), "Field '" + name + "' is found in unexpected category");
            assertNull(testBeanReflection.getCollections().get(name), "Field '" + name + "' is found in unexpected category");
        } else if (fieldKind == Kind.COLLECTION) {
            assertEquals(testBeanReflection.getCollections().get(name), field, "Field '" +
                    name + "' is not specially accessible ");
            assertNull(testBeanReflection.getReferences().get(name), "Field '" + name + "' is found in unexpected category");
            assertNull(testBeanReflection.getFlatFields().get(name), "Field '" + name + "' is found in unexpected category");
        } else if (fieldKind == Kind.REFERENCE) {
            assertEquals(testBeanReflection.getReferences().get(name), field, "Field '" +
                    name + "' is not specially accessible ");
            assertNull(testBeanReflection.getFlatFields().get(name), "Field '" + name + "' is found in unexpected category");
            assertNull(testBeanReflection.getCollections().get(name), "Field '" + name + "' is found in unexpected category");
        } else if (fieldKind == Kind.ID) {
            assertEquals(testBeanReflection.getIdField(), field, "Field '" +
                    name + "' is not specially accessible ");
            assertNull(testBeanReflection.getReferences().get(name), "Field '" + name + "' is found in unexpected category");
            assertNull(testBeanReflection.getFlatFields().get(name), "Field '" + name + "' is found in unexpected category");
            assertNull(testBeanReflection.getCollections().get(name), "Field '" + name + "' is found in unexpected category");
        } else if (fieldKind == Kind.VERSION) {
            assertEquals(testBeanReflection.getVersionField(), field, "Field '" +
                    name + "' is not specially accessible ");
            assertNull(testBeanReflection.getReferences().get(name), "Field '" + name + "' is found in unexpected category");
            assertNull(testBeanReflection.getFlatFields().get(name), "Field '" + name + "' is found in unexpected category");
            assertNull(testBeanReflection.getCollections().get(name), "Field '" + name + "' is found in unexpected category");
        } else {
            throw new IllegalStateException("Field kind is not covered in test: " + fieldKind);
        }

        //If annotations are to be checked:
        if (hasAnnotations != null) {
            List<String> missingAnnotations = new ArrayList<String>();
            for (Class annotation : hasAnnotations) {
                if (!field.hasAnnotation(annotation)) {
                    missingAnnotations.add(annotation.getSimpleName());
                }
            }
            if (!missingAnnotations.isEmpty()) {
                fail("Field '" + name + "' does not have annotations: " + Joiner.on(", ").join(missingAnnotations));
            }
        }
    }

    private void assertCollectionFieldParams(String name, boolean hasGetter, boolean hasSetter,
                                   Class fieldType, Class collectionItemType, Class[] hasAnnotations) {
        //base assertions
        assertFieldParams(name, hasGetter, hasSetter, Kind.COLLECTION, fieldType, hasAnnotations);
        FieldReflection field = testBeanReflection.getField(name);
        assertEquals(field.getCollectionItemClass(), collectionItemType, "Wrong collection item type for field '" + name + "'");
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //                          I N N E R   C L A S S E S
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private static class CollectionItemA {
    }

    private static class CollectionItemB {
    }

    private static class Parent {
    }

    private static class TestBean {
        private List<CollectionItemA> listItems = new ArrayList<CollectionItemA>();
        private Set<CollectionItemB> items = new HashSet<CollectionItemB>();
        private List<String> lines = new ArrayList<>();
        private boolean active;
        private Long id;
        private Integer version;
        private Parent parent;
        private Parent readOnlyParent;
        private Integer readOnlyCode;
        private String writeOnlyPassword;
        private Double weight;

        private Date createdOn;
        private Timestamp modifiedAt;
        private java.sql.Date effectiveFrom;

        private String name;


        public List<CollectionItemA> getListItems() {
            return listItems;
        }

        public void setListItems(List<CollectionItemA> listItems) {
            this.listItems = listItems;
        }

        public Set<CollectionItemB> getItems() {
            return items;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public Parent getParent() {
            return parent;
        }

        public void setParent(Parent parent) {
            this.parent = parent;
        }

        public Parent getReadOnlyParent() {
            return readOnlyParent;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
        }

        public Timestamp getModifiedAt() {
            return modifiedAt;
        }

        public void setModifiedAt(Timestamp modifiedAt) {
            this.modifiedAt = modifiedAt;
        }

        public java.sql.Date getEffectiveFrom() {
            return effectiveFrom;
        }

        public void setEffectiveFrom(java.sql.Date effectiveFrom) {
            this.effectiveFrom = effectiveFrom;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public Integer getReadOnlyCode() {
            return readOnlyCode;
        }

        public void setWriteOnlyPassword(String writeOnlyPassword) {
            this.writeOnlyPassword = writeOnlyPassword;
        }

        public void setLines(List<String> lines) {
            this.lines = lines;
        }

        public List<String> getLines() {
            return lines;
        }
    }

    private static class SetGetter {
        private Set<CollectionItemB> items = new HashSet<CollectionItemB>();

        public Set<CollectionItemB> getItems() {
            return items;
        }
    }
}