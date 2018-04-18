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


import static com.perceptnet.commons.tests.TestGroups.DEBUG;
import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static com.perceptnet.commons.utils.StringUtils.*;
import static org.testng.Assert.*;


/**
 * Created by VKorovkin on 16.03.2015.
 */
public class StringUtilsTest {

    @Test(groups = {UNIT})
    public void testGetTailOrNull() throws Exception {
        assertEquals(getTailOrNull(null, "is"), null, "Wrong result for 'null, is'");
        assertEquals(getTailOrNull("isActive", "is"), "Active", "Wrong result for 'isActive, is'");
        assertEquals(getTailOrNull("isActActive", "Act"), "Active", "Wrong result for 'isActActive, Act'");
        assertEquals(getTailOrNull("isActive", "MISSING"), null, "Wrong result for 'isActive, MISSING'");
        assertEquals(getTailOrNull("", "MISSING"), null, "Wrong result for '\"\", MISSING'");
    }

    @Test(groups = {UNIT})
    public void testGetTail() throws Exception {
        assertEquals(getTail(null, "is"), null, "Wrong result for 'null, is'");
        assertEquals(getTail("isActive", "is"), "Active", "Wrong result for 'isActive, is'");
        assertEquals(getTail("isActive", "Act"), "ive", "Wrong result for 'isActive, Act'");
        assertEquals(getTail("isActive", "MISSING"), "", "Wrong result for 'isActive, MISSING'");
        assertEquals(getTail("", "MISSING"), "", "Wrong result for '\"\", MISSING'");
    }

    @Test(groups = {UNIT})
    public void testGetHeadOrNull() throws Exception {
        assertEquals(getHeadOrNull(null, "is"), null, "Wrong result for 'null, is'");
        assertEquals(getHeadOrNull("", "MISSING"), null, "Wrong result for '\"\", MISSING'");
        assertEquals(getHeadOrNull("isActActive", "Act"), "is", "Wrong result for 'isActActive, Act'");
    }

    @Test(groups = {UNIT})
    public void testGetEndingOrNull() throws Exception {
        assertEquals(getEndingOrNull(null, "is"), null, "Wrong result for 'null, is'");
        assertEquals(getEndingOrNull("isAct_is_ve", "is"), "_ve", "Wrong result for 'isAct_is_ve, is'");
        assertEquals(getEndingOrNull("isActive", "MISSING"), null, "Wrong result for 'isActive, MISSING'");
        assertEquals(getEndingOrNull("", "MISSING"), null, "Wrong result for '\"\", MISSING'");
    }

    @Test(groups = {UNIT})
    public void testIsCyrillic() throws Exception {
        assertEquals(isCyrillic('а'), true);
        assertEquals(isCyrillic('б'), true);
        assertEquals(isCyrillic('я'), true);
        assertEquals(isCyrillic('Б'), true);
        assertEquals(isCyrillic('В'), true);
        assertEquals(isCyrillic('Я'), true);

        assertEquals(isCyrillic('z'), false);
        assertEquals(isCyrillic('1'), false);
        assertEquals(isCyrillic('#'), false);
        assertEquals(isCyrillic('a'), false);
        assertEquals(isCyrillic('Z'), false);
        assertEquals(isCyrillic('A'), false);
    }

    @Test(groups = {UNIT})
    public void testIsCyrillicText() throws Exception {
        assertEquals(isCyrillicText("Я русский бы выучил только за то, что ..."), true, "Wrong result for basic string check");
        assertEquals(isCyrillicText("Я русский бы ZZZZ выучил только за то..."), false, "Wrong result for basic string check");
        assertEquals(isCyrillicText(""), true, "Wrong result for empty string");
        assertEquals(isCyrillicText(null), true, "Wrong result for null string");
    }

    @Test(groups = {UNIT})
    public void testCountCyrillic() throws Exception {
        assertEquals(countCyrillicCharacters("Я рус ZZZZ !!! вы"), 6, "Wrong result for basic string check");
        assertEquals(countCyrillicCharacters(""), 0, "Wrong result for empty string");
        assertEquals(countCyrillicCharacters(null), 0, "Wrong result for null string");
    }

    @Test(groups = {UNIT})
    public void testCountDigits() throws Exception {
        assertEquals(countDigitCharacters("Я рус 123 99 !!! вы"), 5, "Wrong result for Я рус 123 99 !!! вы");
        assertEquals(countDigitCharacters(""), 0, "Wrong result for empty string ");
        assertEquals(countDigitCharacters("7619"), 4, "Wrong result for 7619 ");
        assertEquals(countDigitCharacters("www__aa+-76 19+!"), 4, "Wrong result for www__aa+-76 19+!");
    }


    @Test(groups = {UNIT})
    public void testCutOffTail() throws Exception {
        assertEquals(cutOffTail(null, "Tail"), null, "Wrong result for null, Tail");
        assertEquals(cutOffTail("headTail", "Tail"), "head", "Wrong result for headTail, Tail");
        assertEquals(cutOffTail("headTAIL", "Tail"), null, "Wrong result for headTAIL, Tail");
        assertEquals(cutOffTail("headTailTail", "Tail"), "headTail", "Wrong result for headTail, Tail");
        assertEquals(cutOffTail("Tail", "Tail"), "", "Wrong result for Tail, Tail");
    }

    @Test(groups = {UNIT})
    public void testMultiply() throws Exception {
        assertEquals(multiply("OneTwo", 2), "OneTwoOneTwo", "Wrong result for OneTwo, 2");

    }

    @Test(groups = {DEBUG})
    public void testRemoveSpaces() {
        assertEquals(removeSpaces("  abc  ABS   \nsdf   "), "abcABSsdf");
    }

    @Test(groups = {UNIT})
    public void testCountCharacter() throws Exception {
        assertEquals(countCharacter(",,  ,,", ','), 4, "Wrong result for countCharacter");
        assertEquals(countCharacter(",,  ,,", ' '), 2, "Wrong result for countCharacter");
    }

    @Test(groups = {UNIT})
    public void testContainsOnlyCharacters() throws Exception {
        assertEquals(containsOnlyCharacters(",,  ,,", ", "), true, "Wrong result of containsOnlyCharacters");
        assertEquals(containsOnlyCharacters(",,  ,,", ", 1"), true, "Wrong result of containsOnlyCharacters");
        assertEquals(containsOnlyCharacters(",,  2,,", ", 1"), false, "Wrong result of containsOnlyCharacters");
    }

}
