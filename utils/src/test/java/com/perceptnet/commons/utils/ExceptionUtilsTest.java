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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.perceptnet.commons.tests.TestGroups.*;
import static org.testng.Assert.*;
import static com.perceptnet.commons.utils.ExceptionUtils.getDeepestCause;
import static com.perceptnet.commons.utils.ExceptionUtils.getTopmostCause;


public class ExceptionUtilsTest {
    private Exception testException;

    @BeforeMethod(groups = {UNIT})
    public void setUp() throws Exception {
        testException =
            new IllegalStateException("root",
                new IllegalArgumentException("cause1",
                        new IllegalArgumentException("cause2",
                            new UnsupportedOperationException("cause3"))));

    }

    @Test(groups = {UNIT})
    public void testGetTopmostCause() throws Exception {
        assertNull(getTopmostCause(testException, IOException.class), "Unexpected exception found");
        assertEquals(getTopmostCause(testException, RuntimeException.class).getMessage(), "root", "Wrong topmost cause 1");
        assertEquals(getTopmostCause(testException, IllegalArgumentException.class).getMessage(), "cause1", "Wrong topmost cause 2");
        assertEquals(getTopmostCause(testException, UnsupportedOperationException.class).getMessage(), "cause3", "Wrong topmost cause 3");
    }

    @Test(groups = {UNIT})
    public void testGetDeepestCause() throws Exception {
        assertNull(getDeepestCause(testException, IOException.class), "Unexpected exception found");
        assertNotNull(getDeepestCause(testException, RuntimeException.class), "Deepest cause is not found");
        assertEquals(getDeepestCause(testException, RuntimeException.class).getMessage(), "cause3", "Wrong deepest cause 1");
        assertEquals(getDeepestCause(testException, IllegalArgumentException.class).getMessage(), "cause2", "Wrong deepest cause 2");
        assertEquals(getDeepestCause(testException, UnsupportedOperationException.class).getMessage(), "cause3", "Wrong deepest cause 3");

    }
}