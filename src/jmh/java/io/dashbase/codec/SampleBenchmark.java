package io.dashbase.codec;

import io.dashbase.codec.benchmark.BaseBenchmark;
import org.apache.lucene.store.ByteBuffersDataOutput;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.dashbase.codec.benchmark.BaseBenchmark.SIMDType.SIMD128;

@State(Scope.Thread)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SampleBenchmark {

    ByteBuffersDataOutput addressBuffer = new ByteBuffersDataOutput();

    public BaseBenchmark baseBenchmark = new BaseBenchmark();

    @Param({"128-lucene", "512-simd"})
//    @Param({"128-lucene", "512-simd", "256-simd", "128-simd",})
    private String type;

    @Param({"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "31"})
    private String size;


    @TearDown(Level.Iteration)
    public void tearDown() {
        addressBuffer.reset();
    }

    @Setup
    public void setup() throws IOException {

        baseBenchmark.init(Integer.parseInt(size));

    }


    @Benchmark
    public void decode_512() throws IOException {
        switch (type) {
            case "128-lucene" -> baseBenchmark.luceneDecode();
            case "128-simd" -> baseBenchmark.simdDecode(SIMD128, 4);
            case "256-simd" -> baseBenchmark.simdDecode(SIMD128, 2);
            case "512-simd" -> baseBenchmark.simdDecode(SIMD128, 1);
            default -> throw new IllegalStateException("Unknown type: " + type);
        }
    }

    @Benchmark
    public void encode_512() throws IOException {
        switch (type) {
            case "128-lucene" -> baseBenchmark.luceneEncode();
            case "128-simd" -> baseBenchmark.simdEncode(SIMD128, 4);
            case "256-simd" -> baseBenchmark.simdEncode(SIMD128, 2);
            case "512-simd" -> baseBenchmark.simdEncode(SIMD128, 1);
            default -> throw new IllegalStateException("Unknown type: " + type);
        }
    }


}