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

import com.perceptnet.abstractions.Adaptor;
import com.perceptnet.abstractions.Range;
import com.perceptnet.commons.utils.resource.ResourceMessage;
import com.perceptnet.commons.utils.resource.ResourceString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.perceptnet.commons.utils.ComparableUtils.gt;
import static com.perceptnet.commons.utils.ComparableUtils.le;
import static com.perceptnet.commons.utils.RangeUtils.isInfinity;
import static com.perceptnet.commons.utils.RangeUtils.isNegativeHalfInfinity;
import static com.perceptnet.commons.utils.RangeUtils.isPositiveHalfInfinity;

/**
 * Created by vkorovkin on 17.03.15.
 */
public class RangeOverlappingValidator {

//    public static final String DATE_OVERLAPPING_MESSAGE
//            =  "Диапазон [{0} - {1}] перекрывается с [{2} - {3}]";
//
//    public static final String GAP_MESSAGE
//            =  "Прпуск между диапазонами [{0} - {1}] и [{2} - {3}]";

    public static final ResourceString DATE_OVERLAPPING_MESSAGE
            = new ResourceString("ru.russianpost.util.validators.ranges_overlapping",
            "%s %s пересекается с %s");

    public static final ResourceString GAP_MESSAGE
            = new ResourceString("ru.russianpost.validators.gap_between_ranges",
            "Пропуск между диапазонами %s и %s");

    private int maxProblems = 5;
    private boolean gapsAreAllowed;
    private String rangeMessageLabel = "Диапазон";

    private RangeAdaptor rangeItemAdaptor;
    private Comparator rangeItemComparator;

    /**
     * This adaptor is used to convert range bounds to proper format while formatting validation messages.
     * For example if this field is set, we can format dates in validations messages
     * with this suitable pattern, otherwise they will be passed as is
     */
    private Adaptor rangeBoundMessageAdaptor;

    public RangeOverlappingValidator() {
        this(true, null);
    }

    public RangeOverlappingValidator(boolean gapsAreAllowed, RangeAdaptor rangeItemAdaptor) {
        this.gapsAreAllowed = gapsAreAllowed;
        this.rangeItemAdaptor = rangeItemAdaptor;
        this.rangeItemComparator =
                (rangeItemAdaptor == null)
                        ? new RangeComparator() : new AdaptedRangeComparator<>(rangeItemAdaptor);
    }


    public List<ResourceMessage> validateRanges(List<? extends Range<?>> ranges) {
        if (rangeItemAdaptor != null) {
            throw new UnsupportedOperationException(
                    "This validator is created to validate adapted ranges. Use validateAdaptedRanges");
        }
        return doValidateRanges(ranges);
    }

    public List<ResourceMessage> validateAdaptedRanges(List rangeItems) {
        if (rangeItemAdaptor == null) {
            throw new UnsupportedOperationException(
                    "This validator is not tuned to validate adapted ranges.");
        }
        return doValidateRanges(rangeItems);
    }

    private List<ResourceMessage> doValidateRanges(List ranges) {
        if (ranges.size() < 2) {
            return Collections.emptyList();
        }
        List<ResourceMessage> result = new ArrayList(maxProblems + 1);
        if (!gapsAreAllowed) {
            //To check gaps we need ranges to be sorted, create a copy and sort it:
            ranges = new ArrayList(ranges);
            Collections.sort(ranges, rangeItemComparator);
        }

        for (int i = 0; i < ranges.size() - 1; i ++) {
            Range checkedRange = adaptItemAsRange(ranges.get(i));
            for (int j = i + 1; j < ranges.size(); j++) {
                Range curRange = adaptItemAsRange(ranges.get(j));
                if (overlaps(checkedRange, curRange)) {
                    result.add(msg(DATE_OVERLAPPING_MESSAGE, checkedRange, curRange));
                } else if (!gapsAreAllowed && j == i + 1) {
                    if (checkedRange.getHighBound() != null && curRange.getHighBound() != null
                            && checkedRange.getHighBound().equals(curRange.getLowBound())) {
                        result.add(msg(GAP_MESSAGE, checkedRange, curRange));
                    }
                }
                if (result.size() > maxProblems) {
                    break;
                }
            }
        }

        return result;
    }

    private Range adaptItemAsRange(Object o) {
        return rangeItemAdaptor != null ? (Range) rangeItemAdaptor.adapt(o) : (Range) o;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public int getMaxProblems() {
        return maxProblems;
    }

    public RangeOverlappingValidator setMaxProblems(int maxProblems) {
        this.maxProblems = maxProblems;
        return this;
    }

    public boolean isGapsAreAllowed() {
        return gapsAreAllowed;
    }

    public RangeOverlappingValidator setGapsAreAllowed(boolean gapsAreAllowed) {
        this.gapsAreAllowed = gapsAreAllowed;
        return this;
    }

    public RangeOverlappingValidator setRangeBoundMessageAdaptor(Adaptor rangeBoundMessageAdaptor) {
        this.rangeBoundMessageAdaptor = rangeBoundMessageAdaptor;
        return this;
    }

    public Adaptor getRangeBoundMessageAdaptor() {
        return rangeBoundMessageAdaptor;
    }

    public String getRangeMessageLabel() {
        return rangeMessageLabel;
    }

    public void setRangeMessageLabel(String rangeMessageLabel) {
        this.rangeMessageLabel = rangeMessageLabel;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Checks if there is overlapping between two ranges.
     */
    private boolean overlaps(Range rangeA, Range rangeB) {
        if (isInfinity(rangeA) || isInfinity(rangeB)) {
            return true;
        } else if (isNegativeHalfInfinity(rangeA) && isNegativeHalfInfinity(rangeB)) {
            return true;
        } else if (isPositiveHalfInfinity(rangeA) && isPositiveHalfInfinity(rangeB)) {
            return true;
        } else if (isNegativeHalfInfinity(rangeA) && isPositiveHalfInfinity(rangeB)) {
            return gt(rangeA.getHighBound(), rangeB.getLowBound());
        } else if (isPositiveHalfInfinity(rangeA) && isNegativeHalfInfinity(rangeB)) {
            return gt(rangeB.getHighBound(), rangeA.getLowBound());
        } else {
            return le(rangeA.getLowBound(), rangeB.getLowBound())
                    && le(rangeB.getLowBound(), rangeA.getHighBound())
                    || le(rangeB.getLowBound(), rangeA.getLowBound())
                    && le(rangeA.getLowBound(), rangeB.getHighBound());
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private ResourceMessage msg(ResourceString resourceKey, Range Range1, Range Range2) {
        return new ResourceMessage(resourceKey, rangesToMessageArguments(Range1, Range2));
    }

    private Object[] rangesToMessageArguments(Range range1, Range range2) {
        return new Object[]{rangeMessageLabel, formatRange(range1), formatRange(range2)};
//        return new Object[] {
//                rangeBoundToMessageArgument(range1.getLowBound()),
//                rangeBoundToMessageArgument(range1.getHighBound()),
//                rangeBoundToMessageArgument(range2.getLowBound()),
//                rangeBoundToMessageArgument(range2.getHighBound()),
//        };
    }

    public static String formatRange(Range range) {
        if (Objects.equals(range.getLowBound(), range.getHighBound())) {
            return "" + range.getLowBound();
        } else {
            return "" + range;
        }
    }

    private Object rangeBoundToMessageArgument(Comparable bound) {
        if (bound == null) {
            return null;
        }
        return rangeBoundMessageAdaptor != null ? rangeBoundMessageAdaptor.adapt(bound) : bound;
    }

}
