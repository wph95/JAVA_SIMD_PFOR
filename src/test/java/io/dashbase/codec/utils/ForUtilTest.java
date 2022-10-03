package io.dashbase.codec.utils;

import org.apache.lucene.store.*;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.packed.PackedInts;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class ForUtilTest {

    public static int randomIntBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
    @Test
    public void testForUtil() throws IOException {
        encodeDecode(new ForUtil(), 1);
    }
    @Test
    public void testForUtilV2() throws IOException {
        encodeDecode(new ForUtilV2(), 1);
    }

    public void encodeDecode(BaseForUtil forUtil, int cycle) throws IOException {
        for (int i = 0; i < cycle; i++) {

        }
        final int iterations = 1;
        final int[] values = new int[iterations * forUtil.BLOCK_SIZE];

        for (int i = 0; i < iterations; ++i) {
            final int bpv = 17;
            for (int j = 0; j < forUtil.BLOCK_SIZE; ++j) {
                values[i * forUtil.BLOCK_SIZE + j] = randomIntBetween(0, (int) PackedInts.maxValue(bpv));
            }
        }

        final Directory d = new ByteBuffersDirectory();
        final long endPointer;

        {
            // encode
            IndexOutput out = d.createOutput("test.bin", IOContext.DEFAULT);

            long[] source = new long[forUtil.BLOCK_SIZE];
            long or = 0;
            for (int j = 0; j < forUtil.BLOCK_SIZE; ++j) {
                source[j] = values[j];
                or |= source[j];
            }
            final int bpv = PackedInts.bitsRequired(or);
            out.writeByte((byte) bpv);
            forUtil.encode(source, bpv, out);
            endPointer = out.getFilePointer();
            out.close();
        }

        {
            // decode
            IndexInput in = d.openInput("test.bin", IOContext.READONCE);
            for (int i = 0; i < iterations; ++i) {
                final int bitsPerValue = in.readByte();
                final long currentFilePointer = in.getFilePointer();
                final long[] restored = new long[forUtil.BLOCK_SIZE];
                forUtil.decode(bitsPerValue, in, restored);
                int[] ints = new int[forUtil.BLOCK_SIZE];
                for (int j = 0; j < forUtil.BLOCK_SIZE; ++j) {
                    ints[j] = Math.toIntExact(restored[j]);
                }
                System.out.println(Arrays.toString(ints));
                System.out.println(Arrays.toString(values));

                assertArrayEquals(
                    ArrayUtil.copyOfSubArray(values, i * forUtil.BLOCK_SIZE, (i + 1) * forUtil.BLOCK_SIZE),
                    ints);
                assertEquals(forUtil.numBytes(bitsPerValue), in.getFilePointer() - currentFilePointer);
            }
            assertEquals(endPointer, in.getFilePointer());
            in.close();
        }

        d.close();
    }
}