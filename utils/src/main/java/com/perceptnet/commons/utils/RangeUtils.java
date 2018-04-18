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

import com.perceptnet.abstractions.Range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.perceptnet.commons.utils.ComparableUtils.ge;
import static com.perceptnet.commons.utils.ComparableUtils.gt;
import static com.perceptnet.commons.utils.ComparableUtils.le;
import static com.perceptnet.commons.utils.ComparableUtils.lt;

/**
 * Created by VKorovkin on 06.03.2015.
 */
public class RangeUtils {

    public static List<NumericRange<Integer>> buildRanges(List<Integer> numbers, boolean sortingRequired) {
        List<NumericRange<Integer>> result = new ArrayList<>();
        if (sortingRequired) {
            Collections.sort(numbers);
        }
        Integer curRangeStart = null;
        Integer prevNumber = null;

        for (Integer number : numbers) {
            if (curRangeStart == null) {
                curRangeStart = number;
            } else if (number > prevNumber + 1) {
                result.add(new NumericRange<>(curRangeStart, prevNumber));
                curRangeStart = number;
            }
            prevNumber = number;
        }
        
        if (curRangeStart != null) {
            result.add(new NumericRange<>(curRangeStart, prevNumber));
        }

        return result;
    }

    public static boolean isInfinity(Range range) {
        return range.getLowBound() == null && range.getHighBound() == null;
    }

    public static boolean isHalfInfinity(Range range) {
        return isNegativeHalfInfinity(range) || isPositiveHalfInfinity(range);
    }

    public static boolean isNegativeHalfInfinity(Range range) {
        return range.getLowBound() == null && range.getHighBound() != null;
    }

    public static boolean isPositiveHalfInfinity(Range range) {
        return range.getLowBound() != null && range.getHighBound() == null;
    }

    public static <T extends Comparable> boolean isValueInside(Range<T> range, T value) {
        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        if (range.getLowBound() != null && lt(value, range.getLowBound())) {
            return false;
        } else if (range.getHighBound() != null && gt(value, range.getHighBound())) {
            return false;
        }

        return true;
    }

    public static <T extends Comparable> boolean isValueInside(Collection<Range<T>> ranges, T value) {
        for (Range<T> range : ranges) {
            if (isValueInside(range, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is overlapping between two ranges.
     */
    public static boolean overlaps(Range<Integer> rangeA, Range<Integer> rangeB) {
        if (isInfinity(rangeA) || isInfinity(rangeB)) {
            return true;
        } else if (isNegativeHalfInfinity(rangeA) && isNegativeHalfInfinity(rangeB)) {
            return true;
        } else if (isPositiveHalfInfinity(rangeA) && isPositiveHalfInfinity(rangeB)) {
            return true;
        } else if (isNegativeHalfInfinity(rangeA) && isPositiveHalfInfinity(rangeB)) {
            return ge(rangeA.getHighBound(), rangeB.getLowBound());
        } else if (isPositiveHalfInfinity(rangeA) && isNegativeHalfInfinity(rangeB)) {
            return ge(rangeB.getHighBound(), rangeA.getLowBound());
        } else {
            return le(rangeA.getLowBound(), rangeB.getLowBound())
                    && le(rangeB.getLowBound(), rangeA.getHighBound())
                    || le(rangeB.getLowBound(), rangeA.getLowBound())
                    && le(rangeA.getLowBound(), rangeB.getHighBound());
        }
    }

    public static boolean overlapsWhenOrderedLists(ArrayList<Range<Integer>> rangesA, ArrayList<Range<Integer>> rangesB) {
        if (rangesA == null || rangesA.isEmpty() || rangesB == null || rangesB.isEmpty()) {
            return false;
        }
        Range<Integer> lastRangeA = rangesA.get(rangesA.size() - 1);
        Range<Integer> lastRangeB = rangesB.get(rangesB.size() - 1);
        if (rangesA.get(0).getLowBound() > lastRangeB.getHighBound()
            || rangesB.get(0).getLowBound() > lastRangeA.getHighBound()) {
            return false;
        }

        for (Range<Integer> rangeA : rangesA) {
            if (rangeA.getLowBound() > lastRangeB.getHighBound()) {
                return false;
            }

            for (Range<Integer> rangeB : rangesB) {
                if (rangeB.getLowBound() > rangeA.getHighBound()) {
                    break;
                }
                if (rangeB.getHighBound() < rangeA.getLowBound()) {
                    continue;
                }
                if (overlaps(rangeA, rangeB)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String formatIntRanges(Collection<Range<Integer>> ranges) {
        StringBuilder buff = new StringBuilder();
        for (Range<Integer> range : ranges) {
            if (buff.length() != 0) {
                buff.append(", ");
            }
            if (Objects.equals(range.getLowBound(), range.getHighBound())) {
                buff.append(range.getLowBound());
            } else {
                buff.append(range);
            }
        }
        return buff.toString();
    }

    public static ArrayList<Range<Integer>> parseIntRanges(String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return new ArrayList<>(0);
        }
        String[] pieces = str.split(",");
        ArrayList<Range<Integer>> result = new ArrayList<>(pieces.length);
        for (String piece : pieces) {
            result.add(parseIntRange(piece));
        }
        return result;
    }

    public static Range<Integer> parseIntRange(String str) {
        str = str.replaceAll("\\s+", "");
        if (!str.contains("-")) {
            Integer value = ParseUtils.parseUnsafely(str, Integer.class);
            return new NumericRange<>(value, value);
        } else {
            Integer lowBound = ParseUtils.parseUnsafely(StringUtils.getHeadOrNull(str, "-"), Integer.class);
            Integer highBound = ParseUtils.parseUnsafely(StringUtils.getTailOrNull(str, "-"), Integer.class);
            return new NumericRange<>(lowBound, highBound);
        }
    }

}
