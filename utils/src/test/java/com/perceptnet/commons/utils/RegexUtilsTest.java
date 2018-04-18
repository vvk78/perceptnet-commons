package com.perceptnet.commons.utils;

import org.testng.annotations.Test;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static com.perceptnet.commons.utils.RegexUtils.*;
import static org.testng.Assert.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 09.02.2018
 */
public class RegexUtilsTest {

    @Test(groups = {UNIT})
    public void testSimpleWildcardPattern() throws Exception {
        assertMatches("com.org.Controller", "*Controller");
        assertMatches("com.org.Controller", "*.Controller");
        assertMatches("com.org.Controller", "*.org*.Controller");
        assertMatches("com.org.Controller", "com*org*ller");

        assertNotMatches("com.org.Controller", "org*ller");
        assertNotMatches("com.org.Controller", "*or*llerX");

        assertMatches("com.org.Controller", "?om.org.Controller");
        assertMatches("com.org.Controller", "c?m.org.Controller");
        assertNotMatches("coom.org.Controller", "c?m.org.Controller");
    }

    private void assertMatches(String s, String wildcardPattern) {
        assertTrue(matches(s, wildcardPattern), s + " does not match " + wildcardPattern);
    }

    private void assertNotMatches(String s, String wildcardPattern) {
        assertFalse(matches(s, wildcardPattern), s + " matches " + wildcardPattern);
    }

    private boolean matches(String s, String wildcardPattern) {
        return simpleWildcardPattern(wildcardPattern).matcher(s).matches();
    }



}