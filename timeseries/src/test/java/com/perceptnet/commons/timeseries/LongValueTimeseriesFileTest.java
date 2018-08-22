package com.perceptnet.commons.timeseries;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 21.08.2018
 */
public class LongValueTimeseriesFileTest {
    private File file;

    @BeforeMethod(groups = {UNIT})
    public void beforeMethod() {
        file = new File("unittest");
        if (file.exists() && file.isFile()) {
            file.delete();
        }

    }

    @Test(groups = {UNIT})
    public void testAppend() throws Exception {
        LongValueTimeseriesFile f = new LongValueTimeseriesFile(file, "unittest data");
        long startTs = System.currentTimeMillis() - 100000000L;
        f.append(startTs, 1L);
        f.append(startTs + 10, 22L );
        f.append(startTs + 200, 333L );
        f.append(startTs + 500, 444L );
        f.close();

    }

}