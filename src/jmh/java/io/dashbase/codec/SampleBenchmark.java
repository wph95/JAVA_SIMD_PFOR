package io.dashbase.codec;

import io.dashbase.codec.Utils.MemoryOutput;
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

    public long[] dataL = new long[512];
    public int[] dataI = new int[512];
    public Lucene lucene = new Lucene();
    ByteBuffersDataOutput addressBuffer = new ByteBuffersDataOutput();

    @Param({"512-simd","128-lucene", "256-simd",  "128-simd",})
    private String type;


    @TearDown(Level.Iteration)
    public void tearDown() throws IOException {
        addressBuffer.reset();
    }

    @Setup
    public void setup() throws IOException {
        dataL = new long[512];
        dataI = new int[512];
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 8; j++) {
                dataL[i * 8 + j] += 100 + ((i + j) % 4);
                dataI[i * 8 + j] += 100 + ((i + j) % 4);
            }
        }


    }

    public void simd(VectorSpecies<Integer> s, int size) throws IOException {
        var codec = new BinaryPack(s);
        for (int i = 0; i < size; i++) {
            var v = codec.encode(dataI);
            codec.encode(v);
        }
    }

    public void lucene() throws IOException {
        for (int i = 0; i < 4; i++) {
            var output = new MemoryOutput("test", "test.bin", 512*4);
            lucene.encode(output, dataL);
            var in = output.toInput();
            lucene.decode(in);
        }
    }

    @Benchmark
    public void encode512Int(Blackhole bh) throws IOException {
        switch (type) {
            case "128-lucene" -> lucene();
            case "128-simd" -> simd(IntVector.SPECIES_128, 4);
            case "256-simd" -> simd(IntVector.SPECIES_256, 2);
            case "512-simd" -> simd(IntVector.SPECIES_512, 1);
            default -> throw new IllegalStateException("Unknown type: " + type);
        }
    }


}