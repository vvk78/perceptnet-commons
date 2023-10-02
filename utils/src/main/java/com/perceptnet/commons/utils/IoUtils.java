package com.perceptnet.commons.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * created by vkorovkin on 18.06.2018
 */
public class IoUtils {

    public static String readStreamAsText(InputStream is, int sizeEst, String encoding) {
        try {
            return new String(readStreamAsBytes(is, sizeEst), StringUtils.isBlank(encoding) ? "UTF-8" : encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("" + e + " while converting stream content to text");
        }
    }


    public static byte[] readStreamAsBytes(InputStream is, int sizeEst) {
        final int buffSize = sizeEst < 1000 ? 1000 : sizeEst;
        try {
            List<byte[]> readBuffs = new ArrayList(sizeEst <= 0 ? 100 : 3);
            byte[] buff = new byte[buffSize];
            int totalRead = 0;
            while (is.available() > 0) {
                int bytesRead = is.read(buff);
                if (bytesRead > 0) {
                    totalRead = totalRead + bytesRead;
                    readBuffs.add(Arrays.copyOf(buff, bytesRead));
                }
            }
            if (readBuffs.size() == 1) {
                return readBuffs.get(0);
            }

            byte[] result = new byte[totalRead];
            int offset = 0;
            for (byte[] piece : readBuffs) {
                ArrayUtils.copyTo(piece, result, offset);
                offset = offset + piece.length;
            }
            return result;

        } catch (IOException e) {
            throw new RuntimeException("" + e + " while reading input stream");
        }
    }

    public static void closeSafely(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
