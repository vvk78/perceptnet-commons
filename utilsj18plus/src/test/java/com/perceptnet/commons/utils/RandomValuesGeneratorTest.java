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

import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.assertTrue;

public class RandomValuesGeneratorTest {

    @Test(groups = {UNIT})
    public void generateIntFromRange() throws Exception {
        Set<Integer> allGeneratedValues = new HashSet();
        boolean negativeMet = false;
        boolean positiveMet = false;
        for (int i = 0; i < 100000; i++) {
            int value = RandomValuesGenerator.generateIntForRange(-99, 99);
            assertTrue(value <= 99, "Value is greater than high limit: " + value);
            assertTrue(value >= -99, "Value is lower than low limit: " + value);
            if (value < 0) {
                negativeMet = true;
            } else if (value > 0) {
                positiveMet = true;
            }
            allGeneratedValues.add(value);
        }
        assertTrue(negativeMet, "Negative never generated");
        assertTrue(positiveMet, "Positive never generated");
        assertTrue(allGeneratedValues.size() > 70, "Suspiciously low variety");
    }

    @Test(groups = {UNIT})
    public void generateNegativeIntFromRange() throws Exception {
        Set<Integer> allGeneratedValues = new HashSet();
        boolean negativeMet = false;
        boolean positiveMet = false;
        for (int i = 0; i < 100000; i++) {
            int value = RandomValuesGenerator.generateIntForRange(-90, -10);
            assertTrue(value >= -90, "Value is lower than low limit: " + value);
            assertTrue(value <= -10, "Value is lower than high limit: " + value);
            allGeneratedValues.add(value);
        }
        assertTrue(allGeneratedValues.size() > 50, "Suspiciously low variety");
    }

}