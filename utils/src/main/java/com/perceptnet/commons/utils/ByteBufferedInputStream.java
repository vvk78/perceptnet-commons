package com.perceptnet.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Combines convenience of ByteBuffer with universality of InputStream
 *
 * Created by vkorovkin on 14.03.2017.
 */
public class ByteBufferedInputStream {
    private final InputStream inputStream;
    private final ByteBuffer buffer;
    /** "shortcut" for buffer.array() */
    private final byte[] buffArr;
    private final byte[] auxBuff = new byte[8]; //supposedly there will be nothing more long than long (8 bytes)
    private int read;

    public ByteBufferedInputStream(InputStream inputStream, ByteBuffer buffer) {
        this.inputStream = inputStream;
        this.buffer = buffer;
        this.read = 0;
        this.buffer.clear();
        this.buffArr = buffer.array();
    }

    public int getInt() throws IOException {
        promoteIfHasLessThan(4);
        return buffer.getInt();
    }

    public short getShort() throws IOException {
        promoteIfHasLessThan(2);
        return buffer.getShort();
    }

    public byte get() throws IOException {
        promoteIfHasLessThan(1);
        return buffer.get();
    }

    public void getBytes(byte[] buff) throws IOException {
        getBytes(buff, 0, buff.length);
    }

    public void getBytes(byte[] buff, int offset, int numOfBytes) throws IOException {
        if (numOfBytes <= 0) {
            throw new IllegalArgumentException("Num of bytes is invalid: " + numOfBytes);
        }
        assert buff.length >= numOfBytes + offset : "buffer is not long enough (" + buff.length + " < " + (numOfBytes + offset) + ")";
        int required = numOfBytes;
        do {
            int unreadRest = read - buffer.position();
            int chunkLength = Math.min(unreadRest, required);
            if (chunkLength > 0) {
                System.arraycopy(buffArr, buffer.position(), buff, offset, chunkLength);
                required = required - chunkLength;
                offset = offset + chunkLength;
                buffer.position(buffer.position() + chunkLength);
            }
            if (required > 0) {
                read = inputStream.read(buffArr);
                buffer.clear();
            } else {
                break;
            }
        } while (read > 0);

        if (required > 0) {
            throw new IOException("End of stream reached prematurely. " + required + " bytes of " + numOfBytes + " are absent");
        }
    }

    private void promoteIfHasLessThan(int numOfBytes) throws IOException {
        int unreadRest = read - buffer.position();
        if (unreadRest >= numOfBytes) {
            return;
        }
        assert unreadRest <= auxBuff.length : "Too big unreadRest " + unreadRest;

        if (unreadRest > 0) {
            System.arraycopy(buffArr, buffer.position(), auxBuff, 0, unreadRest);
        }

        read = inputStream.read(buffArr, unreadRest, buffArr.length - unreadRest);

        if (unreadRest > 0) {
            System.arraycopy(auxBuff, 0, buffArr, 0, unreadRest);
            read = read + unreadRest;
        }
        buffer.clear();
    }
}
