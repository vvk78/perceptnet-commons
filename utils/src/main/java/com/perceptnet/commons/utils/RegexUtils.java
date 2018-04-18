package com.perceptnet.commons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 09.02.2018
 */
public class RegexUtils {

    public static Collection<Pattern> patterns(Collection<String> strings) {
        if (strings == null) {
            return null;
        }
        Collection<Pattern> result = new ArrayList<>(strings.size());
        for (String string : strings) {
            Pattern p = Pattern.compile(string);
            result.add(p);
        }
        return result;
    }

    public static Collection<Pattern> simpleWildcardPatterns(Collection<String> strings) {
        if (strings == null) {
            return null;
        }
        Collection<Pattern> result = new ArrayList<>(strings.size());
        for (String string : strings) {
            Pattern p = simpleWildcardPattern(string);
            if (p != null) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Creates classic wildcard s where * stands for any number of any characters and ? stands for exactly one any character
     */
    public static Pattern simpleWildcardPattern(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        StringBuilder mb = new StringBuilder(s.length() + 10);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '*') {
                if (b.length() > 0) {
                    mb.append(Pattern.quote(b.toString()));
                    b = new StringBuilder();
                }
                mb.append(".*");
            } else if (ch == '?') {
                if (b.length() > 0) {
                    mb.append(Pattern.quote(b.toString()));
                    b = new StringBuilder();
                }
                mb.append(".");
            } else {
                b.append(ch);
            }
        }
        if (b.length() > 0) {
            mb.append(Pattern.quote(b.toString()));
        }
        return Pattern.compile(mb.toString());

    }
}
