package com.perceptnet.commons.utils;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.perceptnet.commons.tests.TestGroups.UNIT;
import static org.testng.Assert.assertEquals;

/**
 * Created by vkorovkin on 14.03.2017.
 */
public class ByteBufferedInputStreamTest {
    private static final int BIS_BUFFER_SIZE = 31;

    //test "seeds", number distributes randomly in [0, 100]; they define writtenNumbers
    private int[] seeds = new int[] {10,81,9,64,23,82,35,77,99,53,13,16,1,71,51,75,84,41,87,27,31,48,41,23,84,29,85,67,92,82,4,40,34,20,
            54,21,8,15,46,15,52,40,64,35,46,52,20,26,0,55,89,1,30,2,91,28,82,18,11,17,98,14,23,78,9,39,44,97,75,87,6,72,9,43,22,18,
            23,63,63,8,88,39,92,26,6,5,1,4,47,32,95,49,86,36,97,17,19,52,37,81};

    private ByteBuffer buff; //test bytes
    private List<Number> writtenNumbers; //test numbers to be put as test bytes
    private ByteBufferedInputStream bis;

    @BeforeMethod(groups = {UNIT})
    public void beforeTestMethod() throws Exception {
        buff = ByteBuffer.allocate(500);
        writtenNumbers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            //int r = (int) (Math.random() * 100);
            //System.out.print(r + ",");
            int r = seeds[i];
            if (r < 30) {
                byte b = (byte) r;
                buff.put(b);
                writtenNumbers.add(b);
            } else if (r < 70) {
                short sh = (short) r;
                buff.putShort(sh);
                writtenNumbers.add(sh);
            } else {
                int j = (int) r;
                buff.putInt(j);
                writtenNumbers.add(j);
            }
        }
        //System.out.println();
        InputStream is = new ByteArrayInputStream(buff.array(), 0, buff.position());
        bis = new ByteBufferedInputStream(is, ByteBuffer.allocate(BIS_BUFFER_SIZE));
    }


    @Test(groups = {UNIT})
    public void testBasicReading() throws Exception {
        int i = 0;
        for (Number n : writtenNumbers) {
            if (n instanceof Byte) {
                byte b = (byte) n;
                assertEquals(bis.get(), b, "Error at # " + i);
            } else if (n instanceof Short) {
                short sh = (short) n;
                assertEquals(bis.getShort(), sh, "Error at # " + i);
            } else {
                int j = (int) n;
                assertEquals(bis.getInt(), j, "Error at # " + i);
            }
            i++;
        }
    }

    @Test(groups = {UNIT})
    public void testByteBufferReading() throws Exception {
        //first read couple of single bytes,
        //then read buffer smaller than bis buffer
        //then read buffer of bis buffer size
        //then read read buffer 2.5 times larger than bis buffer
        byte[] arr = buff.array();
        assertEquals(bis.get(), arr[0], "Unexpected starting single byte 0");
        assertEquals(bis.get(), arr[1], "Unexpected starting single byte 1");
        int arrOffset = 2;

        byte[] buff = new byte[3];
        bis.getBytes(buff);
        for (int i = 0; i < buff.length; i++, arrOffset++) {
            assertEquals(buff[i], arr[arrOffset], "Unexpected byte " + i);
        }

        buff = new byte[BIS_BUFFER_SIZE];
        bis.getBytes(buff);

        for (int i = 0; i < buff.length; i++, arrOffset++) {
            assertEquals(buff[i], arr[arrOffset], "Unexpected byte " + i);
        }

        buff = new byte[BIS_BUFFER_SIZE * 2 + 10];
        bis.getBytes(buff);

        for (int i = 0; i < buff.length; i++, arrOffset++) {
            assertEquals(buff[i], arr[arrOffset], "Unexpected byte " + i);
        }
    }



}
