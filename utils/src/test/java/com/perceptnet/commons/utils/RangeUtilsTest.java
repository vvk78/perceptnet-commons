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

import com.perceptnet.abstractions.Range;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static com.perceptnet.commons.utils.RangeUtils.*;

public class RangeUtilsTest {

    @Test(groups = {UNIT})
    public void testIsValueInside() throws Exception {
        assertTrue(isValueInside(nr(1, 99), 5), "Wrong result for isValueInside(nr(1, 99), 5");
        assertFalse(isValueInside(nr(1, 99), 0), "Wrong result for isValueInside(nr(1, 99), 0");
        assertFalse(isValueInside(nr(1, 99), 100), "Wrong result for isValueInside(nr(1, 99), 100");
        assertFalse(isValueInside(nr(null, 99), 100), "Wrong result for isValueInside(nr(1, 99), 100");
        assertTrue(isValueInside(nr(1, null), 100), "Wrong result for isValueInside(nr(1, null), 100)");
        assertTrue(isValueInside(nr(null, 99), 10), "Wrong result for isValueInside(nr(1, null), 100)");
    }

    @Test(groups = {UNIT})
    public void testOverlaps() throws Exception {
        assertTrue(overlaps(nr(1, 99), nr(1, 99)), "Wrong result for overlap check");
        assertTrue(overlaps(nr(1, 99), nr(99, 99)), "Wrong result for overlap check");
        assertTrue(overlaps(nr(1, 99), nr(99, 100)), "Wrong result for overlap check");
        assertTrue(overlaps(nr(1, 99), nr(35, 45)), "Wrong result for overlap check");
        assertTrue(overlaps(nr(1, 99), nr(0, 1)), "Wrong result for overlap check");
        assertTrue(overlaps(nr(1, 99), nr(0, 100)), "Wrong result for overlap check");

        assertFalse(overlaps(nr(1, 99), nr(100, 100)), "Wrong result overlap check");
        assertFalse(overlaps(nr(1, 99), nr(0, 0)), "Wrong result for overlap check");
        assertFalse(overlaps(nr(0, 0), nr(1, 99)), "Wrong result for overlap check");
        assertFalse(overlaps(nr(1, 99), nr(1000, 9900)), "Wrong result for overlap check");
    }



    @Test(groups = {UNIT})
    public void testOverlapsWhenOrderedLists() throws Exception {
        assertTrue(overlapsWhenOrderedLists(
                nrs(nr(10, 10), nr(100, 200)),
                nrs(nr(0, 0), nr(100, 200), nr(300, 400))));

        assertOverlapCheckResultWhenOrdered(true,
                nrs(nr(0, 0)),
                nrs(nr(0, 0)));

        assertOverlapCheckResultWhenOrdered(true,
                nrs(nr(10, 10)),
                nrs(nr(0, 0), nr(10, 10), nr(100, 100)));

        assertOverlapCheckResultWhenOrdered(true,
                nrs(nr(10, 10), nr(100, 200)),
                nrs(nr(0, 0), nr(100, 200), nr(300, 400)));

        assertOverlapCheckResultWhenOrdered(true,
                nrs(nr(400, 500), nr(600, 600)),
                nrs(nr(0, 0), nr(100, 200), nr(300, 400)));

        assertOverlapCheckResultWhenOrdered(true,
                nrs(nr(-100, 0), nr(300, 300), nr(500, 600)),
                nrs(nr(0, 0), nr(100, 200), nr(300, 400)));

        assertOverlapCheckResultWhenOrdered(false,
                nrs(nr(400, 500), nr(600, 600)),
                nrs(nr(0, 0), nr(100, 200), nr(300, 350)));

        assertOverlapCheckResultWhenOrdered(false,
                nrs(nr(-100, 0), nr(300, 300), nr(500, 600)),
                nrs(nr(10, 20), nr(100, 200), nr(350, 400)));

        assertTrue(overlapsWhenOrderedLists(
                nrs(nr(10, 10), nr(100, 200)),
                nrs(nr(0, 0), nr(100, 200), nr(300, 400))));

        assertTrue(overlapsWhenOrderedLists(
                nrs(nr(10, 10)),
                nrs(nr(10, 10))));
    }

    private void assertOverlapCheckResultWhenOrdered(boolean expectedResult, ArrayList<Range<Integer>> rangesA, ArrayList<Range<Integer>> rangesB) {
        if (expectedResult) {
            assertTrue(overlapsWhenOrderedLists(rangesA, rangesB));
            assertTrue(overlapsWhenOrderedLists(rangesB, rangesA), "Looks like symmetry is broken");
        } else {
            assertFalse(overlapsWhenOrderedLists(rangesA, rangesB));
            assertFalse(overlapsWhenOrderedLists(rangesB, rangesA), "Looks like symmetry is broken");
        }
    }

    private static <T extends Number & Comparable> Range<T> nr(T lowBound, T highBound) {
        return new NumericRange(lowBound, highBound);
    }

    private static ArrayList<Range<Integer>> nrs(Range<Integer> ... ranges) {
        return (ArrayList<Range<Integer>>) MiscUtils.asList(ranges);
    }
}