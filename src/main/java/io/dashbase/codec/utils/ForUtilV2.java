package io.dashbase.codec.utils;

import io.dashbase.codec.v2.BitPacking;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;

import java.io.IOException;
import java.util.Arrays;

public class ForUtilV2 extends BaseForUtil {
    public static int BLOCK_SIZE = 128;

    public static int ENCODE_BLOCK_SIZE = 32;
    private static int BLOCK_SIZE_LOG2 = 7;
    public static long[] tmp = new long[BLOCK_SIZE];
    public static byte[] tempByte = new byte[4 * BLOCK_SIZE];

    int numBytes(int bitsPerValue) {
        return bitsPerValue << (BLOCK_SIZE_LOG2 - 3);
    }


    void decodeTo32(int bitsPerValue, DataInput in, long[] longs) throws IOException {
        throw new UnsupportedOperationException();
    }


    public void decode(int bitsPerValue, DataInput in, long[] longs) throws IOException {
        var inArr = new long[bitsPerValue * 4];

        for (int i = 0; i < 4 * bitsPerValue; i++) {
            inArr[i] = in.readInt() & 0xFFFFFFFFL;
        }

        for (int i = 0; i < 4; i++) {
            BitPacking.fastunpack(inArr, bitsPerValue * i, longs, i * ENCODE_BLOCK_SIZE, bitsPerValue);
        }
    }


    public void encode(long[] longs, int bitsPerValue, DataOutput out) throws IOException {
        for (int i = 0; i < 4; i++) {
            BitPacking.fastpackwithoutmask(longs, i * ENCODE_BLOCK_SIZE, tmp, bitsPerValue * i, bitsPerValue);
        }

        long tempV;
        for (int i = 0; i < 4 * bitsPerValue; i++) {
            tempV = tmp[i];
            tempByte[i * 4] = (byte) tempV;
            tempByte[i * 4 + 1] = (byte) (tempV >> 8);
            tempByte[i * 4 + 2] = (byte) (tempV >> 16);
            tempByte[i * 4 + 3] = (byte) (tempV >> 24);
        }

        out.writeBytes(tempByte, 0, 4 * bitsPerValue * 4);

    }


}
