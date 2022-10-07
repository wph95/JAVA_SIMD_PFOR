package io.dashbase.codec.utils;

import io.dashbase.codec.v2.FastPFOR128;
import me.lemire.integercompression.IntWrapper;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;

import java.io.IOException;
import java.util.Arrays;


public class PForUtilV2 implements BasePForUtil {
    public static final int BLOCK_SIZE = 128;
    public FastPFOR128 fastPFOR128 = new FastPFOR128();
    public int[] outArr = new int[BLOCK_SIZE];

    public int[] intArr = new int[BLOCK_SIZE];
    public int[] compressedArr = new int[BLOCK_SIZE];

    public IntWrapper inOffset = new IntWrapper(0);
    public IntWrapper outOffset = new IntWrapper(0);

    public static byte[] tempByte = new byte[4 * BLOCK_SIZE + 32];


    public PForUtilV2() {

    }

    @Override
    public void encode(long[] longs, DataOutput out) throws IOException {
        inOffset.set(0);
        outOffset.set(0);
        for (int i = 0; i < BLOCK_SIZE; i++) {
            intArr[i] = (int) longs[i];
        }
        fastPFOR128.encodePage(intArr, inOffset, BLOCK_SIZE, compressedArr, outOffset);


//        out.writeVInt(outOffset.get());
//        for (int i = 0; i < outOffset.get(); i++) {
//            out.writeInt(compressedArr[i]);
//        }

        addInt(outOffset.get(), 0);
        for (int i = 0; i < outOffset.get(); i++) {
            addInt(compressedArr[i], i * 4 + 4);
        }

        out.writeBytes(tempByte, 0, outOffset.get() * 4 + 4);

    }


    public void addInt(int v, int pos) {
        tempByte[pos] = (byte) v;
        tempByte[pos + 1] = (byte) (v >> 8);
        tempByte[pos + 2] = (byte) (v >> 16);
        tempByte[pos + 3] = (byte) (v >> 24);
    }

    public int readInt(int pos) {
        var v = tempByte[pos] & 0xFF;
        v |= (tempByte[pos + 1] & 0xFF) << 8;
        v |= (tempByte[pos + 2] & 0xFF) << 16;
        v |= (tempByte[pos + 3] & 0xFF) << 24;
        return v;
    }

    @Override
    public void decode(DataInput in, long[] longs) throws IOException {
        inOffset.set(0);
        outOffset.set(0);
        var len = in.readInt() & 0xFFFFFFFFL;

        in.readBytes(tempByte, 0, (int) len * 4);

        for (int i = 0; i < len; i++) {
            compressedArr[i] = readInt(i * 4);
        }

        fastPFOR128.decodePage(compressedArr, inOffset, outArr, outOffset, BLOCK_SIZE);
        for (int i = 0; i < BLOCK_SIZE; i++) {
            longs[i] = outArr[i];
        }
    }

    @Override
    public void decodeAndPrefixSum(DataInput in, long base, long[] longs) throws IOException {
        decode(in, longs);
        longs[0] += base;
        for (int i = 1; i < BLOCK_SIZE; i++) {
            longs[i] += longs[i - 1];
        }
    }

    @Override
    public void skip(DataInput in) throws IOException {
        var len = in.readInt() & 0xFFFFFFFFL;

        in.skipBytes( len * 4);
    }
}
