package com.perceptnet.commons.utils;

import com.perceptnet.commons.tests.TestGroups;
import org.testng.annotations.Test;


import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static com.perceptnet.commons.utils.MiscUtils.split;
import static org.testng.Assert.assertEquals;

public class MiscUtilsTest {

    @Test(groups = {TestGroups.UNIT})
    public void testListSplit() throws Exception {
        final List<Integer> items = Arrays.asList(0, 1, 2, 3, 4);
        assertSplitPieceSizes(split(items, 0), 0, 5);
        assertSplitPieceSizes(split(items, 5), 5, 0);
        assertSplitPieceSizes(split(items, 100), 5, 0);
        assertSplitPieceSizes(split(items, 2), 2, 3);
    }

    @Test(groups = {TestGroups.UNIT})
    public void testFormat() throws Exception {
        System.out.println(MessageFormat.format("Всего слов {0}, уникальных {1}, страниц {2}", 1,2,3));
    }



    private void assertSplitPieceSizes(Pair<List<Integer>, List<Integer>> split, int expectedHeadSize, int expectedTailSize) {
        assertEquals(split.getFirst().size(), expectedHeadSize, "Unexpected list split head size");
        assertEquals(split.getSecond().size(), expectedTailSize, "Unexpected list split tail size");
    }
}
