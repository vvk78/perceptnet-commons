package com.perceptnet.commons.utils;

import java.util.*;
import java.util.function.Supplier;


public final class RandomValuesGenerator {
    private static final Map<Class, Supplier<Object>> typeMatchingMap = new HashMap<>();
    private static final Random random = new Random();

    static {
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


    private RandomValuesGenerator() {
    }

    public static <T> T generateValue(Class<T> clazz, String desiredStringPrefix) {
        if (clazz == null) {
            throw new NullPointerException("Class cannot be bull to generate random value");
        }

        Random random = new Random();
        if (clazz.isEnum()) {
            return clazz.getEnumConstants()[random.nextInt(clazz.getEnumConstants().length)];
        } else if (clazz == String.class) {
            return (T) (desiredStringPrefix + String.valueOf(random.nextInt()));
        }

        if (typeMatchingMap.get(clazz) == null) {
            throw new IllegalArgumentException("Value of class " + clazz + " can not be generated because class is not supported");
        } else {
            return (T) typeMatchingMap.get(clazz).get();
        }
    }

    public static <T> T generateValue(Class<T> clazz) {
        return generateValue(clazz, "string");
    }


    public static int generateIntForRange(int lowLimit, int highLimit) {
        int result = (int) (random.nextDouble() * ((double) (highLimit - lowLimit))) + lowLimit;
        return result;
    }

    public static String generateRussianLetters(int size) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < size; i++) {
            result.append(Character.toString((char) (((char) random.nextInt(64)) + 'А')));
        }
        return result.toString();
    }

    public static <E extends Enum<E>> E randomEnum(Class<E> eClass) {
        E[] enumConstants = eClass.getEnumConstants();
        return enumConstants[random.nextInt(enumConstants.length)];
    }


}



