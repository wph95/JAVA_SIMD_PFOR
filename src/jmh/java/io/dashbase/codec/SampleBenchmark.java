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
    ByteBuffersDataOutput addressBuffer = new ByteBuffersDataOutput();

//    @Param({"simd", "scalar", "simd-avx2", "simd-avx512"})
    @Param({ "128-simd", "256-simd", "512-simd", "128-lucene",})
    private String type;

    final Directory d = new ByteBuffersDirectory();

    @Setup
    public void setup() throws IOException {
        in = new int[512];
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 8; j++) {
                in[i * 8 + j] += 100 + ((i + j) % 4);
            }
        }



    }

    public void simd(VectorSpecies<Integer> s) throws IOException {
        var codec = new BinaryPack(s);
        codec.encode(in);
    }

    public void lucene() throws IOException {
        ByteBuffersIndexOutput output = new ByteBuffersIndexOutput(addressBuffer, "temp", "temp");
        lucene.encode(output, in);

    }

    @Benchmark
    public void testDecode(Blackhole bh) throws IOException {
        switch (type) {
            case "128-lucene" -> lucene();
            case "128-simd" -> simd(IntVector.SPECIES_128);
            case "256-simd" -> simd(IntVector.SPECIES_256);
            case "512-simd" -> simd(IntVector.SPECIES_512);
            default -> throw new IllegalStateException("Unknown type: " + type);
        }
    }


}