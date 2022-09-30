package io.dashbase.local;

import io.dashbase.codec.BinaryPacking;
import io.dashbase.codec.SIMDBinaryPacking;
import io.dashbase.codec.benchmark.BaseSIMDIO;
import jdk.incubator.vector.IntVector;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import static io.dashbase.codec.SIMDBinaryPacking.fastpack17;


@State(Scope.Thread)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SIMDBenchmark {
    final static int VEC_SIZE = 10000;
    final static int SPECIES_SIZE = IntVector.SPECIES_512.length();

    final static int TOTAL = VEC_SIZE * SPECIES_SIZE;
    final static int TOTAL_BYTE = VEC_SIZE * SPECIES_SIZE * 4;


    int[] intArr = new int[TOTAL];
    int[] compressedArr = new int[TOTAL];
    int[] outArr = new int[TOTAL];
    final static MemorySession session = MemorySession.openConfined();
    MemorySegment memorySegment = MemorySegment.allocateNative(TOTAL_BYTE, session);
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(TOTAL_BYTE).order(ByteOrder.nativeOrder());


    IntVector[] intVectors;
    IntVector[] compressedVectors = new IntVector[VEC_SIZE];
    IntVector[] outVectors = new IntVector[TOTAL / SPECIES_SIZE];


    @Setup(Level.Trial)
    public void setup() throws IOException {

        var in = BaseSIMDIO.createRandomArray(TOTAL);

        for (int i = 0; i < TOTAL; i++) {
            memorySegment.set(ValueLayout.JAVA_INT, i * 4L, in[i]);
            byteBuffer.putInt(i * 4, in[i]);

        }
        intVectors = BaseSIMDIO.memorySegment2vector(IntVector.SPECIES_512, memorySegment, TOTAL);


        var size = TOTAL / (SPECIES_SIZE * 32);
        for (int i = 0; i < size; i++) {
            fastpack17(intVectors, i * SPECIES_SIZE, compressedVectors, i * 17);
        }

        for (int i = 0; i < (TOTAL / 32); i += 1) {
            BinaryPacking.fastpack17(intArr, i * 32, compressedArr, i  * 17);
        }



    }

    @Benchmark
    public void memorySegment_to_Vec512() throws IOException {
        BaseSIMDIO.memorySegment2vector(IntVector.SPECIES_512, memorySegment, TOTAL);
    }
    @Benchmark
    public void memorySegment_to_Vec256() throws IOException {
        BaseSIMDIO.memorySegment2vector(IntVector.SPECIES_256, memorySegment, TOTAL);
    }

    @Benchmark
    public void byteBuff_to_IntArr() throws IOException {
        BaseSIMDIO.bytebuf2array(byteBuffer, TOTAL);
    }

    @Benchmark
    public void intBuff_to_IntArr() throws IOException {
        BaseSIMDIO.intbuf2array(byteBuffer, TOTAL);
    }

    @Benchmark
    public void memorySegment_to_IntArr() throws IOException {
        BaseSIMDIO.memorySegment2intArr(memorySegment, TOTAL);
    }


    @Benchmark
    public void simdPack_Encode() {
        var size = TOTAL / (SPECIES_SIZE * 32);
        for (int i = 0; i < size; i++) {
            SIMDBinaryPacking.fastpack17(intVectors, i * SPECIES_SIZE, compressedVectors, i * 17);
        }
    }

    @Benchmark
    public void scalar_Encode() {
        for (int i = 0; i < (TOTAL / 32); i += 1) {
            BinaryPacking.fastpack17(intArr, i * 32, compressedArr, i  * 17);
        }
    }

    @Benchmark
    public void simdPack_Decode() {
        var size = TOTAL / (SPECIES_SIZE * 32);
        for (int i = 0; i < size; i++) {
            SIMDBinaryPacking.fastunpack17(compressedVectors, i * 17, outVectors, i * SPECIES_SIZE);
        }
    }

    @Benchmark
    public void scalar_Decode() {
        for (int i = 0; i < (TOTAL / 32); i += 1) {
            BinaryPacking.fastunpack17(compressedArr, i * 17, outArr, i  * 32);
        }
    }


}