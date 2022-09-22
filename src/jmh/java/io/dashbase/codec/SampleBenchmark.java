package io.dashbase.codec;

import io.dashbase.codec.Utils.MemoryInput;
import io.dashbase.codec.Utils.MemoryOutput;
import io.dashbase.codec.simd.BinaryPack;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import org.apache.lucene.store.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.dashbase.codec.BaseBenchmark.SIMDType.SIMD128;
import static jdk.incubator.vector.IntVector.*;

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


    public byte[] luceneCompressed;
    public IntVector[][] simd128;
    public IntVector[][] simd256;
    public IntVector[][] simd512;

    public BinaryPack codec128 = new BinaryPack(SPECIES_128);
    public BinaryPack codec256 = new BinaryPack(SPECIES_256);
    public BinaryPack codec512 = new BinaryPack(SPECIES_512);


    public BaseBenchmark baseBenchmark = new BaseBenchmark();

    @Param({"128-lucene", "512-simd", "256-simd", "128-simd",})
    private String type;


    @TearDown(Level.Iteration)
    public void tearDown() throws IOException {
        addressBuffer.reset();
    }

    @Setup
    public void setup() throws IOException {

        baseBenchmark.init(7);

    }

    public IntVector[][] simdEncode(VectorSpecies<Integer> s, int size) throws IOException {
        BinaryPack codec = getCodec();
        var compressed = new IntVector[size][32];
        for (int i = 0; i < size; i++) {
            compressed[i] = codec.encodeV(dataI);
        }
        return compressed;
    }

    public BinaryPack getCodec(){
        return switch (type) {
            case "128-simd" -> codec128;
            case "256-simd" -> codec256;
            case "512-simd" -> codec512;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

//    public void simdDecode(VectorSpecies<Integer> s, int size) throws IOException {
//        BinaryPack codec = getCodec();
//        var compressed = new IntVector[size][32];
//        switch (s.vectorBitSize()) {
//            case 128 -> compressed = simd128;
//            case 256 -> compressed = simd256;
//            case 512 -> compressed = simd512;
//            default -> throw new IllegalStateException("Unexpected value: " + s.vectorBitSize());
//        }
//        for (int i = 0; i < size; i++) {
//            codec.decode(compressed[i], 7);
//        }
//    }




    @Benchmark
    public void decode_512_7bit(Blackhole bh) throws IOException {
        switch (type) {
            case "128-lucene" -> baseBenchmark.luceneDecode();
            case "128-simd" -> baseBenchmark.simdDecode(SIMD128, 4);
            case "256-simd" -> baseBenchmark.simdDecode(SIMD128, 2);
            case "512-simd" -> baseBenchmark.simdDecode(SIMD128, 1);
            default -> throw new IllegalStateException("Unknown type: " + type);
        }
    }

    @Benchmark
    public void encode_512_7bit(Blackhole bh) throws IOException {
        switch (type) {
            case "128-lucene" -> baseBenchmark.luceneEncode();
            case "128-simd" -> baseBenchmark.simdEncode(SIMD128, 4);
            case "256-simd" -> baseBenchmark.simdEncode(SIMD128, 2);
            case "512-simd" -> baseBenchmark.simdEncode(SIMD128, 1);
            default -> throw new IllegalStateException("Unknown type: " + type);
        }
    }


}