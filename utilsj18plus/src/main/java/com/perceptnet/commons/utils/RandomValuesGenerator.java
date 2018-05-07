package com.perceptnet.commons.utils;

import com.perceptnet.abstractions.IndexedAccess;

import java.util.*;
import java.util.function.Supplier;


public class RandomValuesGenerator {
    private final Map<Class, Supplier<Object>> typeMatchingMap = new HashMap<>();
    private final Random random = new Random();
    private String stringPrefix = "str";

    protected void fillGeneratorsMap() {
        typeMatchingMap.put(int.class, random::nextInt);
        typeMatchingMap.put(long.class, random::nextLong);
        typeMatchingMap.put(boolean.class, random::nextBoolean);
        typeMatchingMap.put(float.class, random::nextFloat);
        typeMatchingMap.put(short.class, () -> (short) random.nextInt(Short.MAX_VALUE + 1));
        typeMatchingMap.put(double.class, random::nextDouble);
        typeMatchingMap.put(char.class, () -> {
                    int j = 97 + random.nextInt(122 - 97);
                    return (char) j;
                }
        );
        typeMatchingMap.put(Double.class, random::nextDouble);
        typeMatchingMap.put(Float.class, random::nextFloat);
        typeMatchingMap.put(Short.class, () -> (short) random.nextInt(Short.MAX_VALUE + 1));
        typeMatchingMap.put(Character.class, () -> {
                    int j = 97 + random.nextInt(122 - 97);
                    return (char) j;
                }
        );
        typeMatchingMap.put(Integer.class, random::nextInt);
        typeMatchingMap.put(Boolean.class, random::nextBoolean);
        typeMatchingMap.put(Long.class, random::nextLong);
        typeMatchingMap.put(Date.class, Date::new);
        typeMatchingMap.put(java.sql.Date.class, ()->(new java.sql.Date(System.currentTimeMillis())));
    }

    public RandomValuesGenerator() {
        fillGeneratorsMap();
    }

    public String getStringPrefix() {
        return stringPrefix;
    }

    public RandomValuesGenerator setStringPrefix(String stringPrefix) {
        this.stringPrefix = stringPrefix;
        return this;
    }

    public <T> T generateValue(Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Class cannot be bull to generate random value");
        }

        Random random = new Random();
        if (clazz.isEnum()) {
            return (T) randomEnum((Class<? extends Enum>) clazz);
        } else if (clazz == String.class) {
            if (stringPrefix != null && !stringPrefix.isEmpty()) {
                return (T) (stringPrefix + String.valueOf(random.nextInt()));
            } else {
                return (T) String.valueOf(random.nextInt());
            }
        }

        if (typeMatchingMap.get(clazz) == null) {
            throw new IllegalArgumentException("Value of class " + clazz + " can not be generated because class is not supported");
        } else {
            return (T) typeMatchingMap.get(clazz).get();
        }
    }

    public int intForRange(int lowLimit, int highLimit) {
        int result = (int) (random.nextDouble() * ((double) (highLimit - lowLimit))) + lowLimit;
        return result;
    }

    public int intPos(int highLimit) {
        return intForRange(0, highLimit);
    }

    public long longForRange(long lowLimit, long highLimit) {
        long result = (long) (random.nextDouble() * ((double) (highLimit - lowLimit))) + lowLimit;
        return result;
    }

    public String chars(int size, char start, int range) {
        StringBuilder result = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            result.append(Character.toString((char) (((char) random.nextInt(range)) + start)));
        }
        return result.toString();
    }

    public String chars(int size, List<Character> choice) {
        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            result.append(randomItem(choice));
        }
        return result.toString();
    }

    public String chars(int size, IndexedAccess<Character> choice) {
        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            result.append(randomItem(choice));
        }
        return result.toString();
    }

    public String chars(int size, String ... choices) {
        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            result.append(randomItem(new StringIndexedAccess(randomItem(choices))));
        }
        return result.toString();
    }

    public String cyrillic(int size) {
        return chars(size, 'А', 64);
    }

    public String cyrillicLow(int size) {
        return chars(size, 'а', 32);
    }

    public String cyrillicHigh(int size) {
        return chars(size, 'А', 32);
    }

    public String latin(int size) {
        return chars(size, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz");
    }

    public String latinHigh(int size) {
        return chars(size, 'A', 26);
    }

    public String latinLow(int size) {
        return chars(size, 'a', 26);
    }


    public <E extends Enum<E>> E randomEnum(Class<E> eClass) {
        E[] enumConstants = eClass.getEnumConstants();
        return enumConstants[random.nextInt(enumConstants.length)];
    }

    public <T> T randomItem(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }

    public <T> T randomItem(T[] items) {
        return items[random.nextInt(items.length)];
    }

    public <T> T randomItem(IndexedAccess<T> items) {
        return items.get(random.nextInt(items.size()));
    }


}



