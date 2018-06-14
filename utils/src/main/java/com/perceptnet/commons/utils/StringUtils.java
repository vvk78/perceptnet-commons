/*
 * Copyright 2017 Perceptnet
 *
 * This source code is Perceptnet Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 *
 */

package com.perceptnet.commons.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by VKorovkin on 16.03.2015.
 */
public class StringUtils {
    private static final Map<Character, Character> CYRILLIC_LATIN_KEYBOARD_MAPPING = buildCyrillicLatinKeyboardMapping();

    public static String strOrNull(Object o) {
        return o == null ? null : o.toString();
    }

    public static List<String> unquote(List<String> strs) {
        if (strs == null) {
            return null;
        }
        List<String> result = new ArrayList<>(strs.size());
        for (String s : strs) {
            result.add(unquote(s));
        }
        return result;
    }

    public static String unquote(String val) {
        if (val == null) {
            return null;
        }
        if (val.startsWith("\"") && val.endsWith("\"")) {
            val = val.substring(1, val.length() - 1);
        }
        return val;
    }

    public static String multiply(String string, int times) {
        if (times <= 0) {
            throw new IllegalArgumentException("Number of times should be positive");
        }
        if (string == null || string.isEmpty()) {
            return string;
        }
        StringBuilder buff = new StringBuilder(string.length() * times);
        for (int i = 0; i < times; i++) {
            buff.append(string);
        }
        return buff.toString();
    }

    public static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /**
     * Returns tail of the string after <b>first occurrence</b> of given delimiter or null if delimiter is missing.
     * It also returns null if input string is null.
     *
     * @param string
     * @param delimiter
     * @return
     */
    public static String getTailOrNull(String string, String delimiter) {
        if (string == null) {
            return null;
        }
        if (delimiter == null) {
            throw new NullPointerException("delimiter is null");
        }
        int idx = string.indexOf(delimiter);
        if (idx == -1) {
            return null;
        }
        return string.substring(idx + delimiter.length());
    }

    /**
     * Cuts off the tail from given string. If string is null or does not end with this tail returns null.
     *
     * @param string
     * @param tail
     * @return
     */
    public static String cutOffTail(String string, String tail) {
        if (string == null) {
            return null;
        }
        if (tail == null) {
            throw new NullPointerException("delimiter is null");
        }
        if (!string.endsWith(tail)) {
            return null;
        }
        return string.substring(0, string.length() - tail.length());
    }

    /**
     * Returns tail of the string after <b>first occurrence</b> of given delimiter or empty string if delimiter is missing.
     * It returns null if input string is null.
     *
     * @param string
     * @param delimiter
     * @return
     */
    public static String getTail(String string, String delimiter) {
        if (string == null) {
            return null;
        }
        if (delimiter == null) {
            throw new NullPointerException("delimiter is null");
        }
        int idx = string.indexOf(delimiter);
        if (idx == -1) {
            return "";
        }
        return string.substring(idx + delimiter.length());
    }

    /**
     * Returns head of the string after <b>first occurrence</b> of given delimiter or null if delimiter is missing.
     * It also returns null if input string is null.
     *
     * @param string
     * @param delimiter
     * @return
     */
    public static String getHeadOrNull(String string, String delimiter) {
        if (string == null) {
            return null;
        }
        if (delimiter == null) {
            throw new NullPointerException("delimiter is null");
        }
        int idx = string.indexOf(delimiter);
        if (idx == -1) {
            return null;
        }
        return string.substring(0, idx);
    }

    /**
     * Returns ending of the string after <b>last occurrence</b> of given delimiter or null if delimiter is missing.
     * It also returns null if input string is null.
     *
     * @param string
     * @param delimiter
     * @return
     */
    public static String getEndingOrNull(String string, String delimiter) {
        if (string == null) {
            return null;
        }
        if (delimiter == null) {
            throw new NullPointerException("delimiter is null");
        }
        int idx = string.lastIndexOf(delimiter);
        if (idx == -1) {
            return null;
        }
        return string.substring(idx + delimiter.length());
    }

    public static boolean isCyrillic(char ch) {
        return (ch >= 'а' && ch <= 'я') || (ch >= 'А' && ch <= 'Я');
    }

    private static final Set<Character> punctuationChars = getSetOfCharacters(".,!?;:-+*/'*()[]{}<>=\"\\");

    /**
     * Returns true if given string contains only cyrillic characters, digits, whitespaces or punctuation characters.
     * I.e. it is normal cyrillic text.
     *
     * @param str string to check
     */
    public static boolean isCyrillicText(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (!(isCyrillic(ch)
                    || Character.isWhitespace(ch) || Character.isDigit(ch) || punctuationChars.contains(ch))) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsOnlyCyrillicOrAnyOfCharacters(String str, Set<Character> allowedCharsSet) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (!(isCyrillic(ch) || !allowedCharsSet.contains(ch))) {
                return false;
            }
        }
        return true;

    }

    public static boolean containsAnyOfCharacters(String str, Set<Character> allowedCharsSet) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (allowedCharsSet.contains(ch)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsOnlyCharacters(String str, String allowedChars) {
        Set<Character> allowedCharsSet = characterSet(allowedChars);
        for (int i = 0; i < str.length(); i++) {
            Character ch = str.charAt(i);
            if (!allowedCharsSet.contains(ch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsAnyOfCharacters(String str, String allowedChars) {
        return containsAnyOfCharacters(str, characterSet(allowedChars));
    }

    public static boolean containsAnyOfCharactersIgnoreCase(String str, String allowedChars) {
        return containsAnyOfCharacters(str.toLowerCase(), characterSet(allowedChars.toLowerCase()));
    }

    public static Set<Character> characterSet(String str) {
        Set<Character> result = new HashSet<>(str.length());
        for (int i = 0; i < str.length(); i++) {
            result.add(str.charAt(i));
        }
        return result;
    }

    public static boolean containsCyrillicCharacters(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (isCyrillic(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static int countCyrillicCharacters(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (int i = 0; i < str.length(); i++) {
            if (isCyrillic(str.charAt(i))) {
                result++;
            }
        }
        return result;
    }

    public static boolean containsDigitsOnly(String str) {
        return !str.isEmpty() && countDigitCharacters(str) == str.length();
    }

    public static int countDigitCharacters(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                result++;
            }
        }
        return result;
    }

    public static int countCharacter(String str, char ch) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                result++;
            }
        }
        return result;
    }

    public static Set<Character> getSetOfCharacters(String str) {
        Set<Character> result = new HashSet<>(str.length());
        for (int i = 0; i < str.length(); i++) {
            result.add(str.charAt(i));
        }
        return result;
    }

    public static String replaceCharsAccordingToMapping(String str, Map<Character, Character> replacementMap) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder buff = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char curCh = str.charAt(i);
            Character replacement = replacementMap.get(curCh);
            buff.append(replacement == null ? curCh : replacement);
        }
        return buff.toString();
    }

    public static String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static boolean isBlank(String str) {
        if (str != null) {
            int strLen = str.length();
            for (int i = 0; i < strLen; i++) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String fio(String lastName, String firstName, String middleName){
        StringBuilder res = new StringBuilder("");
        if (isNotBlank(lastName)){
            res.append(StringUtils.capitalize(lastName));
        }
        if (isNotBlank(firstName)){
            res.append(' ').append(StringUtils.firstCapitalLetterAndPoint(firstName));
        }
        if (isNotBlank(middleName)){
            res.append(' ').append(StringUtils.firstCapitalLetterAndPoint(middleName));
        }
        return res.toString();
    }


    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }

    public static int compareNullable(String a, String b){
        if (a==b){
            return 0;
        }
        if (a==null){
            return -1;
        }
        if (b==null){
            return 1;
        }
        return a.compareTo(b);
    }

    private static Map<Character, Character> buildCyrillicLatinKeyboardMapping() {
        final String latinChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,-.,.";
        final String cyrillicChars = "ФИСВУАПРШОЛДЬТЩЗЙКЫЕГМЦЧНЯфисвуапршолдьтщзйкыегмцчня0123456789б-юБЮ";

        //just in case - a check:
        if (latinChars.length() != cyrillicChars.length()) {
            throw new IllegalArgumentException("Latin-cyrillic mapping strings length mismatch " +
                    latinChars.length() + " != " + cyrillicChars.length());
        }

        Map<Character, Character> result = new HashMap<Character, Character>();

        for (int i = 0; i < cyrillicChars.length(); i++) {
            result.put(cyrillicChars.charAt(i), latinChars.charAt(i));
        }

        return result;
    }

    /**
     * Auto fix input (remaps it to latin alphabet) if it was with russian language keyboard layout
     */
    public static String fixCyrillicIfNeeded(String qrCode) {
        String result = qrCode;
        for (int i = 0; i < qrCode.length(); i++) {
            char curCh = qrCode.charAt(i);
            if (isCyrillic(curCh)) {
                result = replaceCharsAccordingToMapping(qrCode, CYRILLIC_LATIN_KEYBOARD_MAPPING);
                break;
            } else if (Character.isLetter(curCh)) {
                break;
            }
        }
        return result;
    }

    public static String removeSpaces(String s){
        if (s==null){
            return null;
        }
        return s.replaceAll("\\s+","");
    }



    /**
     *    Replaces a supplied string with blank (empty) one if the supplied
     * string is {@code NULL}. Otherwise returns the supplied string unmodified.
     *
     * @param canBeNull a string supplied (and to be checked if it is {@code NULL}
     *
     * @return empty {@link String} ({@code""}) if the string supplied is
     *         {@code NULL}, otherwise - the supplied string unmodified
     */
    public static String replaceWithBlankIfNull(String canBeNull) {
        if (canBeNull == null) {
            return "";
        }
        return canBeNull;
    }

    public static String inverseCase(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c)) {
                chars[i] = Character.toLowerCase(c);
            } else if (Character.isLowerCase(c)) {
                chars[i] = Character.toUpperCase(c);
            }
        }
        return new String(chars);
    }

    public static String firstCapitalLetterAndPoint(String val){
        if (isBlank(val)){
            return "";
        }
        return val.trim().substring(0,1).toUpperCase()+".";
    }

    public static void expand(StringBuilder builder, Object o) {
        if (o == null || ((o instanceof String) && ((String)o).isEmpty())) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(o);
    }
}