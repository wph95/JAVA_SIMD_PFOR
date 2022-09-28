package io.dashbase.codec.benchmark;

import io.dashbase.codec.CodeC;
import me.lemire.integercompression.synth.ClusteredDataGenerator;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;

public class BaseBenchmark {
    static ClusteredDataGenerator cdg = new ClusteredDataGenerator();


    public static long[][] generateTestData(
        int N, int nbr, int sparsity) {
        final long[][] data = new long[N][];
        final int max = (1 << (nbr + sparsity));
        for (int i = 0; i < N; ++i) {
            data[i] = Arrays.stream(cdg.generateClustered((1 << nbr), max)).mapToLong(value -> (long) value).toArray();
        }
        return data;
    }


    public long[] runCodeC(CodeC codec, long[] data, MemorySegment segment) throws Exception {
        codec.encode(segment, data);
        return codec.decode(segment);
    }


    public void print(long[] expected, long[] actual, int start, int size) {


        System.out.println("expected: " + Arrays.toString(Arrays.copyOfRange(expected, start, start + size)));
        System.out.println("actual:   " + Arrays.toString(Arrays.copyOfRange(actual, start, start + size)));
    }

    public void print(MemorySegment segment, int start, int end) {
        var layout = ValueLayout.JAVA_LONG;
        for (int i = start; i < end; i++) {
            System.out.print(i + ":" + segment.getAtIndex(layout, i)+ ", ");
        }

    }
    public void print(MemorySegment segment) {
        var layout = ValueLayout.JAVA_LONG;
        var offset = 0;
        System.out.print(segment.getAtIndex(layout, 0));
        offset += 1;
        for (int i = 0; i < 10; i++) {
            var bit = segment.getAtIndex(layout, offset);
            System.out.print("bit: " + bit + "   [");
            offset += 1;
            for (int j = 0; j < bit*2; j++) {
                System.out.print(", " + offset +":" + segment.getAtIndex(layout, offset));
                offset += 1;

            }

            System.out.println();


        }

    }

}
