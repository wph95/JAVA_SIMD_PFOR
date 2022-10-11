package io.dashbase.codec.utils;

import com.carrotsearch.randomizedtesting.generators.RandomNumbers;
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

    public static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    @Test
    public void testPForUtil() throws IOException {
        encodeDecode(new PForUtil(new ForUtil()), 1);
    }

    @Test
    public void testPForUtilV2() throws IOException {
        testEncodeDecode(new PForUtilV2());
    }

    @Test
    public void testPForUtilV3() throws IOException {
        testEncodeDecode(new PForUtilV3());
    }

    @Test
    public void testDeltaEncodeDecode() throws IOException {
        testDeltaEncodeDecode(new PForUtilV2());
    }

    public void testEncodeDecode(BasePForUtil pForUtil) throws IOException {
        final int size = 10000 * 128;
        final int[] values = createTestData(10000, 30);
        final int iterations = size / pForUtil.BLOCK_SIZE;

        final Directory d = new ByteBuffersDirectory();
        final long endPointer = encodeTestData(pForUtil, iterations, values, d);

        IndexInput in = d.openInput("test.bin", IOContext.READONCE);
        for (int i = 0; i < iterations; ++i) {
            if (randomIntBetween(0, 5) == 0) {
                pForUtil.skip(in);
                continue;
            }
            final long[] restored = new long[pForUtil.BLOCK_SIZE];
            pForUtil.decode(in, restored);
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

        d.close();
    }


    public void testDeltaEncodeDecode(BasePForUtil pForUtil) throws IOException {
        final int iterations = RandomNumbers.randomIntBetween(random(), 50, 1000);
        // cap at 31 - 7 bpv to ensure we don't overflow when working with deltas (i.e., 128 24 bit
        // values treated as deltas will result in a final value that can fit in 31 bits)
        final int[] values = createTestData(iterations, 31 - 7);

        final Directory d = new ByteBuffersDirectory();
        final long endPointer = encodeTestData(pForUtil, iterations, values, d);

        IndexInput in = d.openInput("test.bin", IOContext.READONCE);
        for (int i = 0; i < iterations; ++i) {
            if (random().nextInt(5) == 0) {
                pForUtil.skip(in);
                continue;
            }
            long base = 0;
            final long[] restored = new long[128];
            pForUtil.decodeAndPrefixSum(in, base, restored);
            final long[] expected = new long[128];
            for (int j = 0; j < 128; ++j) {
                expected[j] = values[i * 128 + j];
                if (j > 0) {
                    expected[j] += expected[j - 1];
                } else {
                    expected[j] += base;
                }
            }
            assertArrayEquals(expected, restored);
        }
        assertEquals(endPointer, in.getFilePointer());
        in.close();

        d.close();
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
            pForUtil.encode(source, out);
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
                pForUtil.decode(in, restored);
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


    private int[] createTestData(int iterations, int maxBpv) {
        final int[] values = new int[iterations * 128];

        for (int i = 0; i < iterations; ++i) {
            final int bpv = TestUtil.nextInt(random(), 0, maxBpv);
            for (int j = 0; j < 128; ++j) {
                values[i * 128 + j] =
                    RandomNumbers.randomIntBetween(random(), 0, (int) PackedInts.maxValue(bpv));
                if (random().nextInt(100) == 0) {
                    final int exceptionBpv;
                    if (random().nextInt(10) == 0) {
                        exceptionBpv = Math.min(bpv + TestUtil.nextInt(random(), 9, 16), maxBpv);
                    } else {
                        exceptionBpv = Math.min(bpv + TestUtil.nextInt(random(), 1, 8), maxBpv);
                    }
                    values[i * 128 + j] |= random().nextInt(1 << (exceptionBpv - bpv)) << bpv;
                }
            }
        }

        return values;
    }

    private long encodeTestData(BasePForUtil pForUtil, int iterations, int[] values, Directory d) throws IOException {
        IndexOutput out = d.createOutput("test.bin", IOContext.DEFAULT);

        for (int i = 0; i < iterations; ++i) {
            long[] source = new long[pForUtil.BLOCK_SIZE];
            for (int j = 0; j < pForUtil.BLOCK_SIZE; ++j) {
                source[j] = values[i * pForUtil.BLOCK_SIZE + j];
            }
            pForUtil.encode(source, out);
        }
        final long endPointer = out.getFilePointer();
        out.close();

        return endPointer;
    }


}