package com.perceptnet.commons.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com); on 027 27.05.2024
 */


public class IdFormattingUtils {
    public static String formatAsId(String str, IdFormattingStyle style, CamelCaseAcronymHandling camelCaseAcronymHandling) {
        if (camelCaseAcronymHandling == null) {
            camelCaseAcronymHandling = style == IdFormattingStyle.CAMEL_CASE_LOW ? CamelCaseAcronymHandling.KEEP_NOT_STARTING : CamelCaseAcronymHandling.REFORMAT;
        }
        List<String> parts = idParts(str);
        if (parts.isEmpty()) {
            return "";
        }
        switch (style) {
            case CAMEL_CASE_UP:
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < parts.size(); i++) {
                    String s = parts.get(i);
                    if ((camelCaseAcronymHandling == CamelCaseAcronymHandling.KEEP_NOT_STARTING && i > 0
                            || camelCaseAcronymHandling == CamelCaseAcronymHandling.KEEP) && isUppercase(s, true)) {
                        result.append(s);
                    } else {
                        result.append(s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase());
                    }
                }
                return result.toString();
            case CAMEL_CASE_LOW:
                result = new StringBuilder();
                for (int i = 0; i < parts.size(); i++) {
                    String s = parts.get(i);
                    if ((camelCaseAcronymHandling == CamelCaseAcronymHandling.KEEP_NOT_STARTING && i > 0
                            || camelCaseAcronymHandling == CamelCaseAcronymHandling.KEEP) && isUppercase(s, true)) {
                        result.append(s);
                    } else if (i == 0) {
                        result.append(s.toLowerCase());
                    } else {
                        result.append(s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase());
                    }
                }
                return result.toString();
            default:
                result = new StringBuilder();
                for (String part : parts) {
                    result.append(swapCase(part, style.isUpperCase()));
                    result.append(style.getSeparator());
                }
                return result.substring(0, result.length() - style.getSeparator().length());
        }
    }

    static List<String> idParts(String str) {
        List<String> result = new ArrayList<>();
        String[] split = str.split("[_\\-\\s]");
        for (String s : split) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            result.addAll(splitAtCaseShift(s, true));
        }
        return result;
    }

    static String swapCase(String str, Boolean upperCase) {
        if (upperCase == null) {
            return str;
        } else if (upperCase) {
            return str.toUpperCase();
        } else {
            return str.toLowerCase();
        }
    }

    static boolean isUppercase(String s, boolean caseAgnosticDigits) {
        for (char ch : s.toCharArray()) {
            if (!(Character.isUpperCase(ch) || (caseAgnosticDigits && Character.isDigit(ch)))) {
                return false;
            }
        }
        return !s.isEmpty();
    }

    static boolean isLowercase(String s, boolean caseAgnosticDigits) {
        for (char ch : s.toCharArray()) {
            if (!(Character.isLowerCase(ch) || (caseAgnosticDigits && Character.isDigit(ch)))) {
                return false;
            }
        }
        return !s.isEmpty();
    }

    static boolean isLowercase(char ch, boolean caseAgnosticDigits) {
        return Character.isLowerCase(ch) || (caseAgnosticDigits && Character.isDigit(ch));
    }

    static boolean isUppercase(char ch, boolean caseAgnosticDigits) {
        return Character.isUpperCase(ch) || (caseAgnosticDigits && Character.isDigit(ch));
    }


    static List<String> splitAtCaseShift(String s, boolean fixAcronyms) {
        List<String> result = new ArrayList<>();
        Boolean low = null;
        StringBuilder buff = new StringBuilder();

        for (char ch : s.toCharArray()) {
            Boolean chLow = isLowercase(ch, true);
            if (!chLow.equals(low) && (buff.length() > 1 || (!chLow && buff.length() == 1 && isLowercase(buff.charAt(0), true)))) {
                if (buff.length() > 0) {
                    if (result.isEmpty() || !fixAcronyms) {
                        result.add(buff.toString());
                    } else {
                        String prev = result.get(result.size() - 1);
                        if (prev.length() < 2 || !isUppercase(prev, true)) {
                            result.add(buff.toString());
                        } else {
                            result.set(result.size() - 1, prev.substring(0, prev.length() - 1));
                            result.add(prev.substring(prev.length() - 1) + buff.toString());
                        }
                    }
                    buff = new StringBuilder();
                }
            }
            low = chLow;
            buff.append(ch);
        }
        if (buff.length() > 0) {
            result.add(buff.toString());
        }
        return result;
    }
}








