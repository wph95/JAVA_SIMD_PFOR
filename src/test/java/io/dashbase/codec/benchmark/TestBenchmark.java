package io.dashbase.codec.benchmark;

import io.dashbase.codec.CodeC;
import io.dashbase.codec.Lucene;
import io.dashbase.codec.simd.BinaryPackCodeC;
import jdk.incubator.vector.IntVector;
import me.lemire.integercompression.BinaryPacking;
import me.lemire.integercompression.Composition;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.VariableByte;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.util.Arrays;

import static io.dashbase.codec.benchmark.BaseBenchmark.generateTestData;
import static org.junit.jupiter.api.Assertions.*;

public class TestBenchmark {

    public BaseBenchmark bs = new BaseBenchmark();
    MemorySession session = MemorySession.openConfined();

    static class Result {
        public long compressedSize = 0;
        public long encodeTime = 0;
        public long decodeTime = 0;
    }


    @Test
    public void testLucene() throws Exception {
        var longData = generateTestData(1, 16, 10)[0];
        var intData = Arrays.stream(longData).mapToInt(value -> (int) value).toArray();

        var segment = MemorySegment.allocateNative((2 << 18) * 8, session);
        var codec = new Lucene();
        var compressedSize = codec.encode(segment, longData);
        var out = codec.decode(segment);

        System.out.println("LUCENE: " + Arrays.toString(out));
        System.out.println((float) compressedSize / (longData.length * 8L));
        assertArrayEquals(longData, out);


    }

    @Test
    public void testSimdCodeC() throws Exception {
        var longData = generateTestData(1, 16, 10)[0];
        var intData = Arrays.stream(longData).mapToInt(value -> (int) value).toArray();

        var segment = MemorySegment.allocateNative((2 << 18) * 8, session);
        var codec = new BinaryPackCodeC(IntVector.SPECIES_512);
        var compressedSize = codec.encode(segment, intData);
        System.out.println("SIMD: ");
        System.out.println((float) compressedSize / (longData.length * 8L));
        var out = codec.decodeInt(segment);
        assertArrayEquals(intData, out);

    }

    @Test
    public void testBenchmark() throws IOException {
        var cycle = 100;
        var longData = generateTestData(cycle, 16, 10);

        var simd = new CodeCWrap(new BinaryPackCodeC(IntVector.SPECIES_512), "simd");
        runBench(100, longData, simd);
        var lucene = new CodeCWrap(new Lucene(), "lucene");
        runBench(100, longData, lucene);
        runJavaPFor(100, longData);

    }

    void runJavaPFor(int cycle, long[][] longData) throws IOException {
        var intData = Arrays.stream(longData).map(value -> Arrays.stream(value).mapToInt(v -> (int) v).toArray()).toArray(int[][]::new);

        var codec = new Composition(new BinaryPacking(), new VariableByte());

        int maxLength = intData[0].length;

        IntWrapper inpos = new IntWrapper();
        IntWrapper outpos = new IntWrapper();
        int[] compressBuffer = new int[4 * maxLength + 1024];
        int[] decompressBuffer = new int[maxLength + 1024];

        var simdResults = new Result();
        for (int j = 0; j < cycle; j++) {

            for (int i = 0; i < cycle; i++) {
                inpos.set(0);
                outpos.set(0);
                var data = intData[i];

                var start = System.nanoTime();
                codec.compress(data, inpos, data.length, compressBuffer, outpos);
                var encodeTime = System.nanoTime() - start;


                final int compressedSize = outpos.get() + 1;


                inpos.set(0);
                outpos.set(1);
                start = System.nanoTime();
                codec.uncompress(compressBuffer, inpos,
                                 compressedSize - 1, decompressBuffer,
                                 outpos);
                var decodeTime = System.nanoTime() - start;

                simdResults.compressedSize += compressedSize;
                simdResults.encodeTime += encodeTime;
                simdResults.decodeTime += decodeTime;


            }
        }

        var run_count = cycle * cycle;
        System.out.println("JavaPFor" + ": ");
        System.out.println((float) simdResults.compressedSize / (100 * (2 << 16) * 4L));
        System.out.println(simdResults.encodeTime / run_count);
        System.out.println(simdResults.decodeTime / run_count);


    }

    void runBench(int cycle, long[][] longData, CodeCWrap codec) throws IOException {
        var intData = Arrays.stream(longData).map(value -> Arrays.stream(value).mapToInt(v -> (int) v).toArray()).toArray(int[][]::new);
        var segment = MemorySegment.allocateNative((2 << 18) * 8, session);

        // warm up
        for (int i = 0; i < cycle; i++) {
            codec.encode(segment, longData[i], intData[i]);
            codec.decode(segment);
        }
        System.out.println("finish warm up");

        var simdResults = new Result();
        for (int j = 0; j < cycle; j++) {
            for (int i = 0; i < cycle; i++) {

                var start = System.nanoTime();
                var compressedSize = codec.encode(segment, longData[i], intData[i]);
                var encodeTime = System.nanoTime() - start;


                start = System.nanoTime();
                codec.decode(segment);
                var decodeTime = System.nanoTime() - start;


                simdResults.compressedSize += compressedSize;
                simdResults.encodeTime += encodeTime;
                simdResults.decodeTime += decodeTime;
            }
        }


        var run_count = cycle * cycle;
        var name = codec.type;


        System.out.println(codec.type + ": ");
        System.out.println((float) simdResults.compressedSize / (100 * (2 << 16) * 4L));
        System.out.println(simdResults.encodeTime / run_count);
        System.out.println(simdResults.decodeTime / run_count);
    }


    public class CodeCWrap {
        public CodeC codec;
        public String type;

        public CodeCWrap(CodeC codec, String type) {
            this.codec = codec;
            this.type = type;
        }

        public long encode(MemorySegment segment, long[] longData, int[] intData) throws IOException {
            if (type.equals("simd")) {
                return codec.encode(segment, intData);
            } else {
                return codec.encode(segment, longData);
            }
        }

        public void decode(MemorySegment segment) throws IOException {
            if (type.equals("simd")) {
                codec.decodeInt(segment);
            } else {
                codec.decode(segment);
            }
        }
    }

}
