package com.perceptnet.commons;

import com.perceptnet.abstractions.IndexedAccess;
import com.perceptnet.commons.utils.ArrayIndexedAccessProxy;
import com.perceptnet.commons.utils.Pair;
import org.testng.annotations.Test;

import java.util.Random;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.*;

/**
 * created by vkorovkin on 03.05.2018
 */
public class BlendedIndexedAccessTest {
    private final Random random = new Random();

//    @Test(groups = {UNIT})
    public void testBlendedIndexedAccess() {
        int totalSize;
        BlendedIndexedAccess<Integer> a;
        {
            Pair<IndexedAccess[], Integer> d = data(10000, 0, 100);
            IndexedAccess[] data = d.getFirst();
            totalSize = d.getSecond();
            a = new BlendedIndexedAccess(data);
        }
        assertEquals(a.size(), totalSize, "Wrong total size");
        for (int i = 0; i < totalSize; i++) {
            assertEquals(a.get(i), Integer.valueOf(i), "Unexpected item at " + i);
        }

    }

    private Pair<IndexedAccess[], Integer> data(int num, int lowBatchSize, int maxBatchSize) {
        IndexedAccess[] result = new IndexedAccess[num];
        int offset = 0;
        for (int i = 0; i < num; i++) {
            int batchSize = intForRange(lowBatchSize, maxBatchSize);
            Integer[] batch = new Integer[batchSize];
            for (int k = 0; k < batchSize; k++) {
                batch[k] = offset + k;
            }
            offset += batchSize;
            result[i] = new ArrayIndexedAccessProxy(batch);
        }
        return new Pair(result, offset);
    }

    public int intForRange(int lowLimit, int highLimit) {
        int result = (int) (random.nextDouble() * ((double) (highLimit - lowLimit))) + lowLimit;
        return result;
    }



}