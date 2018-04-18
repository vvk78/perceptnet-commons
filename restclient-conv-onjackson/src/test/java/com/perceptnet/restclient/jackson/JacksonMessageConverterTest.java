package com.perceptnet.restclient.jackson;

import org.testng.annotations.Test;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 25.03.2018
 */
public class JacksonMessageConverterTest {

    @Test(groups = {UNIT})
    public void testFormat() throws Exception {
        JacksonMessageConverter c = new JacksonMessageConverter();
        System.out.println(c.format(new Object[] {"ччсссс", "попап", true}));
    }

}