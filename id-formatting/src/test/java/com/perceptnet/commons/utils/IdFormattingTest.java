package com.perceptnet.commons.utils;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com); on 027 27.05.2024
 */
public class IdFormattingTest {

    @Test
    public void shouldSplitIdParts() {
        String s = "IT  __  IS - Some Familiar Word _";
        assertEquals(Arrays.asList("IT", "IS", "Some", "Familiar", "Word"), IdFormattingUtils.idParts(s), "Id parts split is wrong");
    }

    @Test
    public void shouldSplitOnCaseShift() {
        assertSplitAtCaseShift("Familiar", "Familiar");
        assertSplitAtCaseShift("familiar", "familiar");
        assertSplitAtCaseShift("FAMILIAR", "FAMILIAR");
        assertSplitAtCaseShift("FamiliarWord", "Familiar", "Word");
        assertSplitAtCaseShift("familiarWord", "familiar", "Word");
        assertSplitAtCaseShift("SomeFamiliarWord", "Some", "Familiar", "Word");
        assertSplitAtCaseShift("someFamiliarWord", "some", "Familiar", "Word");
        assertSplitAtCaseShift("anAppricot", "an", "Appricot");
        assertSplitAtCaseShift("aFamiliarWord", "a", "Familiar", "Word");
        assertSplitAtCaseShift("SomeFamiliarWordA", "Some", "Familiar", "Word", "A");
        assertSplitAtCaseShift("SomeFamiliarWordA1", "Some", "Familiar", "Word", "A1");
        assertSplitAtCaseShift("SomeFAMILIAR", "Some", "FAMILIAR");
    }

    private void assertSplitAtCaseShift(String... s) {
        List<String> sl = Arrays.asList(s);
        String str = sl.get(0);
        List<String> expectedSplit = sl.subList(1, sl.size());
        assertEquals(IdFormattingUtils.splitAtCaseShift(str, true), expectedSplit, str + " was not split on case change as expected");
    }

    @Test
    public void shouldFormatAsId() {
        String s = "IT  __  IS - Some Familiar Word _";
        assertIdFormat("ItIsSomeFamiliarWord", IdFormattingStyle.CAMEL_CASE_UP, s);
        assertIdFormat("ITISSomeFamiliarWord", IdFormattingStyle.CAMEL_CASE_UP, CamelCaseAcronymHandling.KEEP, s);

        assertIdFormat("itIsSomeFamiliarWord", IdFormattingStyle.CAMEL_CASE_LOW, s);
        assertIdFormat("ITISSomeFamiliarWord", IdFormattingStyle.CAMEL_CASE_LOW, CamelCaseAcronymHandling.KEEP, s);

        assertIdFormat("IT_IS_Some_Familiar_Word", IdFormattingStyle.SNAKE_CASE, s);
        assertIdFormat("IT_IS_SOME_FAMILIAR_WORD", IdFormattingStyle.SNAKE_CASE_UP, s);
        assertIdFormat("it_is_some_familiar_word", IdFormattingStyle.SNAKE_CASE_LOW, s);

        assertIdFormat("IT-IS-Some-Familiar-Word", IdFormattingStyle.KEBAB_CASE, s);
        assertIdFormat("IT-IS-SOME-FAMILIAR-WORD", IdFormattingStyle.KEBAB_CASE_UP, s);
        assertIdFormat("it-is-some-familiar-word", IdFormattingStyle.KEBAB_CASE_LOW, s);

        String rnsS = "RNSFamiliarId";
        assertIdFormat("RNSFamiliarId", IdFormattingStyle.CAMEL_CASE_LOW, CamelCaseAcronymHandling.KEEP, rnsS);
        assertIdFormat("rnsFamiliarId", IdFormattingStyle.CAMEL_CASE_LOW, CamelCaseAcronymHandling.KEEP_NOT_STARTING, rnsS);

        assertIdFormat("RnsFamiliarId", IdFormattingStyle.CAMEL_CASE_UP, CamelCaseAcronymHandling.KEEP_NOT_STARTING, rnsS);
        assertIdFormat("RNSFamiliarId", IdFormattingStyle.CAMEL_CASE_UP, CamelCaseAcronymHandling.KEEP, rnsS);
        assertIdFormat("RnsFamiliarId", IdFormattingStyle.CAMEL_CASE_UP, CamelCaseAcronymHandling.REFORMAT, rnsS);
    }

    @Test
    public void shouldProperlySplitAtCaseShiftWithDigit() {
        assertEquals(IdFormattingUtils.splitAtCaseShift("FamiliarIdentifier2", true), Arrays.asList("Familiar", "Identifier2"));
    }

    @Test
    public void shouldProperlyDefineIdPartsWithDigit() {
        assertEquals(IdFormattingUtils.idParts("FamiliarIdentifier2"), Arrays.asList("Familiar", "Identifier2"));
    }

    @Test
    public void shouldReformatAcronymOnFirstPlaceWhenCamelCaseUp() {
        String rnsS = "RNSFamiliarId";
        assertIdFormat("RnsFamiliarId", IdFormattingStyle.CAMEL_CASE_UP, CamelCaseAcronymHandling.REFORMAT, rnsS);
    }

    @Test
    public void shouldDetectCasesOfChars() {
        assertEquals(IdFormattingUtils.isLowercase('7', true), true);
        assertEquals(IdFormattingUtils.isLowercase('7', false), false);
        assertEquals(IdFormattingUtils.isLowercase('a', true), true);
        assertEquals(IdFormattingUtils.isLowercase('a', false), true);
        assertEquals(IdFormattingUtils.isLowercase('A', true), false);
        assertEquals(IdFormattingUtils.isLowercase('A', false), false);

        assertEquals(IdFormattingUtils.isUppercase('7', true), true);
        assertEquals(IdFormattingUtils.isUppercase('7', false), false);
        assertEquals(IdFormattingUtils.isUppercase('a', true), false);
        assertEquals(IdFormattingUtils.isUppercase('a', false), false);
        assertEquals(IdFormattingUtils.isUppercase('A', true), true);
        assertEquals(IdFormattingUtils.isUppercase('A', false), true);
    }

    @Test
    public void shouldDetectCasesOfStrings() {
        assertEquals(IdFormattingUtils.isLowercase("7", true), true);
        assertEquals(IdFormattingUtils.isLowercase("7", false), false);
        assertEquals(IdFormattingUtils.isLowercase("a7", true), true);
        assertEquals(IdFormattingUtils.isLowercase("a7", false), false);
        assertEquals(IdFormattingUtils.isLowercase("a", true), true);
        assertEquals(IdFormattingUtils.isLowercase("a", false), true);
        assertEquals(IdFormattingUtils.isLowercase("A", true), false);
        assertEquals(IdFormattingUtils.isLowercase("A", false), false);

        assertEquals(IdFormattingUtils.isUppercase("7", true), true);
        assertEquals(IdFormattingUtils.isUppercase("7", false), false);
        assertEquals(IdFormattingUtils.isUppercase("a", true), false);
        assertEquals(IdFormattingUtils.isUppercase("a", false), false);
        assertEquals(IdFormattingUtils.isUppercase("A", true), true);
        assertEquals(IdFormattingUtils.isUppercase("A", false), true);
        assertEquals(IdFormattingUtils.isUppercase("a7", true), false);
        assertEquals(IdFormattingUtils.isUppercase("a7", false), false);
        assertEquals(IdFormattingUtils.isUppercase("A7", true), true);
        assertEquals(IdFormattingUtils.isUppercase("A7", false), false);
    }

    private void assertIdFormat(String expected, IdFormattingStyle style, String s) {
        assertIdFormat(expected, style, CamelCaseAcronymHandling.REFORMAT, s);
    }

    private void assertIdFormat(String expected, IdFormattingStyle style, CamelCaseAcronymHandling acronymHandling, String s) {
        assertEquals(IdFormattingUtils.formatAsId(s, style, acronymHandling), expected, "String not formatted as id as expected");
    }
}
