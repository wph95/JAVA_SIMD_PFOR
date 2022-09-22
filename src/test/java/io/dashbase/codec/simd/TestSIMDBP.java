package io.dashbase.codec.simd;

import io.dashbase.codec.Utils.MemoryOutput;
import jdk.incubator.vector.IntVector;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class TestSIMDBP {

    public int[] createData(int size) {
        var MAX = 1 << (size + 1) - 1;
        int[] data = new int[256];
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 8; j++) {
                data[i * 8 + j] += Math.abs(MAX - 256 + i * 8 + j) % MAX;
            }
        }
        return data;
    }

    public void testBitSize(int size) throws IOException {
        var in = createData(size);
        var codec = new BinaryPack(IntVector.SPECIES_256);
        var out = codec.test(in, size);
        assertArrayEquals(in, out);
    }

    @Test
    public void testBit() throws IOException {
        for (int i = 1; i < 33; i++) {
            testBitSize(i);
        }
    }

    @Test
//    TODO: 2021/3/31
    public void testBitPackWithIndexIO() throws IOException {
        var VECTOR_LENGTH = IntVector.SPECIES_256.length();
        for (int bitSize = 7; bitSize < 33; bitSize++) {
            var in = createData(bitSize);
            var codec = new BinaryPack(IntVector.SPECIES_256);

            System.out.println("bitSize: " + bitSize);
            var compressedOutput = new MemoryOutput("test", "test", 9126);


            codec.encode(in, compressedOutput);
            var mid = codec.encodeV(in);
            System.out.println("m1:  " + Arrays.toString(codec.toArr(mid, 7)));

            var mid2 = new int[bitSize * 8];

            var compressed = compressedOutput.toInput();


            var out = codec.decode(compressed, 7);
            System.out.println(Arrays.toString(in));
            System.out.println(Arrays.toString(out));
            assertArrayEquals(in, out);

        }
    }

}
