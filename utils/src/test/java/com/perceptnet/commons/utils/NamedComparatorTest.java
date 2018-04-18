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

import com.perceptnet.abstractions.Named;
import org.testng.annotations.Test;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class NamedComparatorTest {

    @Test(groups = {UNIT})
    public void testCompareUsual() throws Exception {
        assertEquals(0, compare(false, true, "A", "A"), "Wrong A-A result");
        assertTrue(compare(false, true, "a", "A") > 0, "Wrong a-A result");
        assertTrue(compare(false, true, "A", "a") < 0, "Wrong A-a result");
    }

    @Test(groups = {UNIT})
    public void testCompareIgnoreCase() throws Exception {
        assertEquals(0, compare(true, true, "AA", "AA"), "Wrong AA-AA result");
        assertEquals(0, compare(true, true, "aA", "Aa"), "Wrong aA-Aa result");
        assertEquals(0, compare(true, true, "A", "a"), "Wrong A-a result");
        assertTrue(compare(true, true, "aa", "A") > 0, "Wrong aa-A result");
    }

    @Test(groups = {UNIT})
    public void testCompareUsualWithNulls() throws Exception {
        assertEquals(0, compare(false, true, null, null), "Wrong 2 nulls result");
        assertEquals(1, compare(false, true, null, "A"), "Wrong null-A result");
        assertEquals(-1, compare(false, true, "A", null), "Wrong A-null result");

        assertEquals(0, compare(false, false, null, null), "Wrong 2 nulls result");
        assertEquals(-1, compare(false, false, null, "A"), "Wrong null-A result");
        assertEquals(1, compare(false, false, "A", null), "Wrong A-null result");
    }

    @Test(groups = {UNIT})
    public void testCompareIgnoreCaseWithNulls() throws Exception {
        assertEquals(0, compare(true, true, null, null), "Wrong 2 nulls result");
        assertEquals(1, compare(true, true, null, "A"), "Wrong null-A result");
        assertEquals(-1, compare(true, true, "A", null), "Wrong A-null result");

        assertEquals(0, compare(true, false, null, null), "Wrong 2 nulls result");
        assertEquals(-1, compare(true, false, null, "A"), "Wrong null-A result");
        assertEquals(1, compare(true, false, "A", null), "Wrong A-null result");
    }



    private int compare(boolean ignoreCase, boolean nullGreater, String n1, String n2) {
        return new NamedComparator(ignoreCase, nullGreater).compare(item(n1), item(n2));
    }

    private NamedItem item(String name) {
        return new NamedItem(name);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //                                          I N N E R       C L A S S E S
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private static final class NamedItem implements Named {
        private String name;

        public NamedItem(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}