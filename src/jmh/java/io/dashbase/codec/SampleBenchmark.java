package io.dashbase.codec;

import io.dashbase.codec.simd.BinaryPack;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import org.apache.lucene.store.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SampleBenchmark {

    public int[] in = new int[256];
    public Lucene lucene = new Lucene();
    IndexInput input;
    IndexOutput output;

    @Param({"simd", "scalar", "simd-avx2", "simd-avx512"})
    private String type;

    final Directory d = new ByteBuffersDirectory();

    @TearDown
    public void tearDown() throws IOException {
        input.close();
        output.close();
    }
    @Setup
    public void setup() throws IOException {
        in = new int[256];
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 8; j++) {
                in[i * 8 + j] += 100 + ((i + j) % 4);
            }
        }



    }

    public void simd(VectorSpecies<Integer> s) throws IOException {
        var codec = new BinaryPack(s);
        output = d.createOutput("test.bin", IOContext.DEFAULT);
        output.close();
        input =  d.openInput("test.bin", IOContext.READONCE);
        d.deleteFile("test.bin");
    }

    public void lucene() throws IOException {
        output = d.createOutput("test.bin", IOContext.DEFAULT);
        lucene.encode(output, in);
        output.close();
        input =  d.openInput("test.bin", IOContext.READONCE);
        lucene.decode(input);
        d.deleteFile("test.bin");

    }

    @Benchmark
    public void test(Blackhole bh) throws IOException {
        switch (type) {
            case "scalar" -> lucene();
            case "simd" -> simd(IntVector.SPECIES_128);
            case "simd-avx2" -> simd(IntVector.SPECIES_256);
            case "simd-avx512" -> simd(IntVector.SPECIES_512);
            default -> throw new IllegalStateException("Unknown type: " + type);
        }
    }


}