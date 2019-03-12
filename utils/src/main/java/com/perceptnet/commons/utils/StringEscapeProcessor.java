package com.perceptnet.commons.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 10.03.2019
 */
public class StringEscapeProcessor {
    private Set<Character> escapedChars;
    private char escapeChar;

    public StringEscapeProcessor(String escapedChars, char escapeChar) {
        this(escapedChars.toCharArray(), escapeChar);
    }

    public StringEscapeProcessor(char[] escapedChars, char escapeChar) {
        this.escapeChar = escapeChar;
        this.escapedChars = new HashSet<Character>(escapedChars.length + 1);
        for (int i = 0; i < escapedChars.length; i++) {
            this.escapedChars.add(escapedChars[i]);
        }
        this.escapedChars.add(escapeChar);
    }

    public String escapeString(String string) {
        if (string == null) {
            return null;
        }
        StringBuilder buff = new StringBuilder(string.length() + 10);
        for (int i = 0; i < string.length(); i++) {
            char curChar = string.charAt(i);
            if (isEscapedChar(curChar)) {
                buff.append(escapeChar);
            }
            buff.append(curChar);
        }
        return buff.toString();
    }

    public String unescape(String string) {
        return unescape(string, 0);
    }

    public String unescape(String string, int beginIndex) {
        if (string == null) {
            return null;
        }
        if (beginIndex < 0 || beginIndex >= string.length()) {
            throw new IndexOutOfBoundsException("Index " + beginIndex + " is out of bounds");
        } else if (string.length() == beginIndex + 1) {
            if (isEscapedChar(string.charAt(beginIndex))) {
                throw new IllegalArgumentException("Error in escape sequence: '" + string + "'");
            }
            return "" + string.charAt(beginIndex);
        }
        StringBuilder buff = new StringBuilder(string.length());
        Character nextChar = null;
        for (int i = 0; i < string.length() - 1; i++) {
            char curChar = string.charAt(i);
            nextChar = string.charAt(i+1);
            if (curChar == escapeChar) {
                if (!isEscapedChar(nextChar)) {
                    throw new IllegalArgumentException("Error in escape sequence: '" + string + "'");
                }
                buff.append(nextChar);
                nextChar = null;
                i++;
            } else {
                buff.append(curChar);
            }
        }
        if (nextChar != null) {
            buff.append(nextChar);
        }
        return buff.toString();
    }

    public int unescapedIndexOf(String string, char ch) {
        return unescapedIndexOf(string, ch, 0);
    }

    public int unescapedIndexOf(String string, char ch, int fromIndex) {
        int result = -1;
        if (!isEscapedChar(ch)) {
            return string.indexOf(ch, fromIndex);
        }
        Character nextChar = null;
        for (int i = fromIndex; i < string.length() - 1; i++) {
            char curChar = string.charAt(i);
            nextChar = string.charAt(i + 1);
            if (escapeChar == ch) {
                if (curChar == escapeChar && !isEscapedChar(nextChar)) {
                    return i;
                }
            } else {
                if (curChar == escapeChar) {
                    if (!isEscapedChar(nextChar)) {
                        throw new IllegalArgumentException("Error in escape sequence: '" + string + "'");
                    }
                    nextChar = null;
                    i++;
                } else {
                    if (curChar == ch) {
                        return i;
                    }
                }
            }
        }
        if (nextChar != null && nextChar == ch) {
            return string.length() - 1;
        }

        return -1;
    }

    private boolean isEscapedChar(Character ch) {
        if (ch == null) {
            return false;
        }
        return escapedChars.contains(ch);
    }
}
