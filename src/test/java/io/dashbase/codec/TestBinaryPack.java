package io.dashbase.codec;

import io.dashbase.codec.Utils.MemoryOutput;
import org.apache.lucene.store.*;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.packed.PackedInts;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static io.dashbase.codec.ForUtil.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TestBinaryPack {
    final ForUtil forUtil = new ForUtil();

    public Lucene lucene = new Lucene();

    @Test
    public void test() throws IOException {
        var output = new MemoryOutput("test", "test.bin", 512*4);
        var data = new long[512];
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 8; j++) {
                data[i * 8 + j] += 100 + ((i + j) % 4);
            }
        }
        var values = Arrays.copyOfRange(data, 0, 128);
        lucene.encode(output, values);
        var in = output.toInput();
        var results = lucene.decode(in);
        System.out.println(Arrays.toString(values));
        System.out.println(Arrays.toString(results));

    }
}
