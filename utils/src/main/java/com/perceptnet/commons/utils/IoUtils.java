package com.perceptnet.commons.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * created by vkorovkin on 18.06.2018
 */
public class IoUtils {

    public static String readStreamAsText(InputStream is, int sizeEst, String encoding) {
        try {
            return new String(readStreamAsBytes(is, sizeEst), StringUtils.isBlank(encoding) ? "UTF-8" : encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e + " while converting stream content to text");
        }
    }


    public static byte[] readStreamAsBytes(InputStream is, int sizeEst) {
        final int buffSize = sizeEst < 1000 ? 1000 : sizeEst;
        try {
            List<byte[]> readBuffs = new ArrayList<>(sizeEst <= 0 ? 100 : 3);
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

    public static void writeAsStringsToFile(Collection<?> items, String fileName, String separator, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            if (!items.isEmpty()) {
                Writer w = new OutputStreamWriter(fos);
                boolean addSeparator = false;
                for (Object o : items) {
                    if (o != null) {
                        if (addSeparator) {
                            w.write(separator);
                        }
                        w.write(o.toString());
                        addSeparator = true;
                    }
                }
                w.flush();
            }
            fos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to file " + fileName + ": ");
        } finally {
            closeSafely(fos);
        }
    }

    public static void writeToFile(byte[] items, String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            if (items.length > 0) {
                fos.write(items);
            }
            fos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to file " + fileName + ": ");
        } finally {
            closeSafely(fos);
        }
    }

    public static byte[] readFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IllegalStateException("'" + fileName + "' file does not exusts");
        }
        if (!file.isFile()) {
            throw new IllegalStateException("'" + fileName + "' is not a fiile");
        }
        if (file.length() >= 100_000_000) {
            throw new IllegalStateException("'" + fileName + "' is too big for this method (" + file.length() + ")");
        }
        int size = (int) file.length();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] result = new byte[size];
            int read = is.read(result);
            if (read != size) {
                throw new IllegalStateException("Only " + read + " bytes read from file '" + fileName + "' when " + size + " were expected");
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read from file '" + fileName + "' due to " + e, e);
        } finally {
            closeSafely(is);
        }
    }

    public static String readFileAsText(String fileName, String encoding) {
        try {
            return new String(readFile(fileName), StringUtils.isBlank(encoding) ? "UTF-8" : encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e + " while reading '" + fileName + "' as text");
        }
    }

}
