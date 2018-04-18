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

import com.perceptnet.abstractions.Adaptor;
import com.perceptnet.abstractions.Range;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.perceptnet.commons.utils.resource.ResourceMessage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RangeOverlappingValidatorTest {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private RangeOverlappingValidator validator;

    @BeforeClass(groups = {UNIT})
    public void setupTest() {
        validator = new RangeOverlappingValidator();
        validator.setRangeBoundMessageAdaptor(new Adaptor<Date, String>() {
            @Override
            public String adapt(Date date) {
                return sdf.format(date);
            }
        });
    }

    @Test(groups = {UNIT})
    public void testSimpleDateRangesOverlapping() throws Exception {
        assertProblems(validator, 0, new Range[0]);

        assertProblems(validator, 0,
                period(null, null)
        );

        assertProblems(validator, 1,
                period(null, null),
                period("01/01/2012", null)
        );

        assertProblems(validator, 1,
                period(null, null),
                period(null, "01/01/2012")
        );

        assertProblems(validator, 1,
                period(null, "01/01/2012"),
                period(null, null)
        );

        assertProblems(validator, 0,
                period(null, "01/01/2012"),
                period("01/01/2012", null)
        );

        assertProblems(validator, 1,
                period(null, "02/01/2012"),
                period("01/01/2012", null)
        );

        assertProblems(validator, 1,
                period("01/01/2012", "02/01/2012"),
                period("01/01/2012", null)
        );

        assertProblems(validator, 2,
                period("01/01/2012", "02/01/2012"),
                period("02/01/2012", "05/01/2012"),
                period("03/01/2012", "04/01/2012")
        );

        assertProblems(validator, 5,
                period("01/01/2012", "02/01/2012"),
                period("02/01/2012", "05/01/2012"),
                period("02/01/2012", "05/01/2012"),
                period("03/01/2012", "04/01/2012")
        );
    }

    //todo add tests for adapted version


    @Test(groups = {UNIT})
    public void testFormattingMessages() throws Exception {
        List<ResourceMessage> problems = validator.validateRanges(
                periodsAsList(period("01/01/2012", "02/01/2012"),
                        period("02/01/2012", "05/01/2012"),
                        period("02/01/2012", "06/01/2012"),
                        period("03/01/2012", "04/01/2012")));
        assertEquals(problems.size(), 5);
        assertTrue(problems.get(0).toString().contains("02/01/2012 - 05/01/2012"), "Wrong validation message");
        assertTrue(problems.get(0).toString().contains("02/01/2012 - 05/01/2012"), "Wrong validation message");
    }

    private void assertProblems(RangeOverlappingValidator validator, int expected, Range<Date>... periods) throws Exception {
        List<ResourceMessage> problems = validator.validateRanges(periodsAsList(periods));
        assertEquals(problems.size(), expected, "Not expected num of problems. Problems:\n" + Joiner.on("\n").join(problems));
    }

    private List<Range<Date>> periodsAsList(Range<Date>... periods) {
        return Arrays.asList(periods);
    }


    private Range<Date> period(final String start, final String end) throws Exception {

        return new Range<Date>() {
            @Override
            public Date getLowBound() {
                try {
                    return start == null ? null : sdf.parse(start);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Date getHighBound() {
                try {
                    return end == null ? null : sdf.parse(end);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String toString() {
                return start + " - " + end;
            }
        };
    }

}