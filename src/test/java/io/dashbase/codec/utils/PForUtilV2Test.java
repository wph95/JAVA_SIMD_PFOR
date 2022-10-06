package io.dashbase.codec.utils;

import org.apache.lucene.store.*;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.packed.PackedInts;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class PForUtilV2Test {

    public static int randomIntBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
    @Test
    public void testPForUtil() throws IOException {
        encodeDecode(new PForUtil(new ForUtil()), 1);
    }

    @Test
    public void testPForUtilV2() throws IOException {
        encodeDecode(new PForUtilV2(), 1);
    }

    public void encodeDecode(BasePForUtil pForUtil, int cycle) throws IOException {
        for (int i = 0; i < cycle; i++) {

        }
        final int iterations = 1;
        final int[] values = new int[iterations * pForUtil.BLOCK_SIZE];

        for (int i = 0; i < iterations; ++i) {
            final int bpv = 17;
            for (int j = 0; j < pForUtil.BLOCK_SIZE; ++j) {
                values[i * pForUtil.BLOCK_SIZE + j] = randomIntBetween(0, (int) PackedInts.maxValue(bpv));
            }
        }

        final Directory d = new ByteBuffersDirectory();
        final long endPointer;

        {
            // encode
            IndexOutput out = d.createOutput("test.bin", IOContext.DEFAULT);

            long[] source = new long[pForUtil.BLOCK_SIZE];
            long or = 0;
            for (int j = 0; j < pForUtil.BLOCK_SIZE; ++j) {
                source[j] = values[j];
                or |= source[j];
            }
            final int bpv = PackedInts.bitsRequired(or);
            out.writeByte((byte) bpv);
            pForUtil.encode(source,  out);
            endPointer = out.getFilePointer();
            out.close();
        }

        {
            // decode
            IndexInput in = d.openInput("test.bin", IOContext.READONCE);
            for (int i = 0; i < iterations; ++i) {
                final int bitsPerValue = in.readByte();
                final long currentFilePointer = in.getFilePointer();
                final long[] restored = new long[pForUtil.BLOCK_SIZE];
                pForUtil.decode( in, restored);
                int[] ints = new int[pForUtil.BLOCK_SIZE];
                for (int j = 0; j < pForUtil.BLOCK_SIZE; ++j) {
                    ints[j] = Math.toIntExact(restored[j]);
                }

                assertArrayEquals(
                    ArrayUtil.copyOfSubArray(values, i * pForUtil.BLOCK_SIZE, (i + 1) * pForUtil.BLOCK_SIZE),
                    ints);
            }
            assertEquals(endPointer, in.getFilePointer());
            in.close();
        }

        d.close();
    }

}