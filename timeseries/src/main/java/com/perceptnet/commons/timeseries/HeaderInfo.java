package com.perceptnet.commons.timeseries;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import static com.perceptnet.commons.utils.MiscUtils.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 16.08.2018
 */
class HeaderInfo {
    /**
     * Byte size of one item block. Cannot be lesser than 8 (timestamp)
     */
    private final byte itemDataBlockSize;

    /**
     * Label of the file.
     */
    private final String label;

    /**
     * Additional info in string form, may be in json format
     */
    private final String additionalInfo;

    HeaderInfo(int itemDataBlockSize, String label, String additionalInfo) {
        this.itemDataBlockSize = (byte)itemDataBlockSize;
        this.label = label != null ? label : "";
        this.additionalInfo = additionalInfo != null ? additionalInfo : "";
    }

    public int getItemDataBlockSize() {
        return itemDataBlockSize;
    }

    static HeaderInfo readHeaderInfoFromBytes(byte[] bytes) {
        try {
            ByteBuffer b = ByteBuffer.wrap(bytes);
            byte itemDataBlockSize = b.get();

            String label;
            int labelLength = byteToLength(b.get());
            if (labelLength == 0) {
                label = "";
            } else {
                byte[] labelBuff = new byte[labelLength];
                b.get(labelBuff);
                label = new String(labelBuff, "UTF-8");
            }

            int addInfoLength = byteToLength(b.get());
            String additionalInfo;
            if (addInfoLength == 0) {
                additionalInfo = "";
            } else {
                byte[] addInfoBuff = new byte[addInfoLength];
                b.get(addInfoBuff);
                additionalInfo = new String(addInfoBuff, "UTF-8");
            }

            return new HeaderInfo(itemDataBlockSize, label, additionalInfo);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    byte[] writeToBytes(byte[] bytes) {
        try {
            byte[] labelBytes = label.getBytes("UTF-8");
            byte[] adiBytes = additionalInfo.getBytes("UTF-8");
            if (labelBytes.length + adiBytes.length + 3 > bytes.length) {
                throw new RuntimeException("Buffer length (" + bytes.length + ") is not sufficient. Label length " + labelBytes.length +
                        ", additional info length " + adiBytes.length + ")");
            }
            ByteBuffer b = ByteBuffer.wrap(bytes);
            b.put(itemDataBlockSize);
            b.put(lengthToByte(labelBytes.length));
            b.put(labelBytes);
            b.put(lengthToByte(adiBytes.length));
            b.put(adiBytes);
            return bytes;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


}
