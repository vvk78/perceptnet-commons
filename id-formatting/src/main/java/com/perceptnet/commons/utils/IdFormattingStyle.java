package com.perceptnet.commons.utils;

/**
 * created by vkorovkin (vkorovkin@gmail.com); on 027 27.05.2024
 */
public enum IdFormattingStyle {
    CAMEL_CASE_UP("", null),
    CAMEL_CASE_LOW("", null),
    SNAKE_CASE("_", null),
    SNAKE_CASE_UP("_", true),
    SNAKE_CASE_LOW("_", false),
    KEBAB_CASE("-", null),
    KEBAB_CASE_UP("-", true),
    KEBAB_CASE_LOW("-", false);

    private final String separator;
    private final Boolean upperCase;

    IdFormattingStyle(String separator, Boolean upperCase) {
        this.separator = separator;
        this.upperCase = upperCase;
    }

    public String getSeparator() {
        return separator;
    }

    public Boolean isUpperCase() {
        return upperCase;
    }
}
