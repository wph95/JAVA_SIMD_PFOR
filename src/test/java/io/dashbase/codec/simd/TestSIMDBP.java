package io.dashbase.codec.simd;

import jdk.incubator.vector.IntVector;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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

}
