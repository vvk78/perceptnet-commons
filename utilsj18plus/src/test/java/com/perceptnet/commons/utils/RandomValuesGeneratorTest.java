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
        Set<Integer> allGeneratedValues = new HashSet<>();
        boolean negativeMet = false;
        boolean positiveMet = false;
        RandomValuesGenerator g = new RandomValuesGenerator();
        for (int i = 0; i < 100000; i++) {
            int value = g.intForRange(-99, 99);
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
        Set<Integer> allGeneratedValues = new HashSet<>();
        boolean negativeMet = false;
        boolean positiveMet = false;
        RandomValuesGenerator g = new RandomValuesGenerator();
        for (int i = 0; i < 100000; i++) {
            int value = g.intForRange(-90, -10);
            assertTrue(value >= -90, "Value is lower than low limit: " + value);
            assertTrue(value <= -10, "Value is lower than high limit: " + value);
            allGeneratedValues.add(value);
        }
        assertTrue(allGeneratedValues.size() > 50, "Suspiciously low variety");
    }

    @Test(groups = {UNIT})
    public void testLatin() {
        RandomValuesGenerator g = new RandomValuesGenerator();
        String str = g.latin(500);
//        System.out.println("------------");
//        for (char ch = 'A'; ch <= 'Z'; ch++) {
//            System.out.print(ch);
//        }
//        System.out.println("------------");
//        for (char ch = 'a'; ch <= 'z'; ch++) {
//            System.out.print(ch);
//        }
//        System.out.println("------------");
//        System.out.println("Latin characters: " + str);
//        System.out.println("------------");

        boolean hasLow = false;
        boolean hasHigh = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            boolean chLow = ch >= 'a' && ch <= 'z';
            boolean chHigh = (ch >= 'A' && ch <= 'Z');
            assertTrue(chHigh || chLow, "Wrong char: " + ch);

            hasLow = hasLow || chLow;
            hasHigh = hasHigh || chHigh;
        }
        assertTrue(hasLow, "Not a single low char");
        assertTrue(hasHigh, "Not a single high char");
    }

    @Test(groups = {UNIT})
    public void testCyrillic() {
        RandomValuesGenerator g = new RandomValuesGenerator();
        String str = g.cyrillic(500);
        System.out.println("Сyrillic characters: " + str);
        boolean hasLow = false;
        boolean hasHigh = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            boolean chLow = ch >= 'а' && ch <= 'я';
            boolean chHigh = (ch >= 'А' && ch <= 'Я');
            assertTrue(chHigh || chLow, "Wrong char: " + ch);

            hasLow = hasLow || chLow;
            hasHigh = hasHigh || chHigh;
        }
        assertTrue(hasLow, "Not a single low char");
        assertTrue(hasHigh, "Not a single high char");

    }


}