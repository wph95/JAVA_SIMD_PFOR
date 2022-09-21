package io.dashbase.codec.simd;

import io.dashbase.codec.simd.BinaryPack;
import jdk.incubator.vector.IntVector;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class TestSIMDBP {

    public int[] createData(int size) {
        var MAX = size << (size+1) - 1;
        int[] data = new int[256];
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 8; j++) {
                data[i * 8 + j] += ((i*8 + j)% 128);
            }
        }
        return data;
    }

    @Test
    public void testBitSize(int size) throws IOException {
        var in = createData(size);
        var codec = new BinaryPack(IntVector.SPECIES_256);
        var out = codec.test(in);
        System.out.println(Arrays.toString(in));
        System.out.println(Arrays.toString(out));
        assertArrayEquals(in, out);
    }

}
