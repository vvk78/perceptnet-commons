package com.perceptnet.commons.utils;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 09.02.2018
 */
public class IncExlRegexFilterTest {
    private IncExlRegexFilter f;

    @BeforeMethod(groups = {UNIT})
    public void beforeMethod() throws Exception {
        f = new IncExlRegexFilter();
    }

    @Test(groups = {UNIT})
    public void testIsIncluded() throws Exception {
        f.setIncFiltersSimpleWildcards(Arrays.asList(new String[]{
            "com.aim.polyglot.rest.*Controller"
        }));
        f.setExlFiltersSimpleWildcards(Arrays.asList(new String[]{
                "*PlainController"
        }));

        assertTrue(f.isIncluded("com.aim.polyglot.rest.crm.CrmController"), "Wrong filtering");
        assertFalse(f.isIncluded("com.aim.polyglot.rest.crm.CrmPlainController"), "Wrong filtering");
    }

}