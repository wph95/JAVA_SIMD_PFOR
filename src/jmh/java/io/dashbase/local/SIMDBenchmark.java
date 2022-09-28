package io.dashbase.local;

import io.dashbase.codec.benchmark.BaseSIMDIO;
import jdk.incubator.vector.IntVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;


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
    final static MemorySession session = MemorySession.openConfined();
    MemorySegment memorySegment = MemorySegment.allocateNative(TOTAL_BYTE, session);
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(TOTAL_BYTE).order(ByteOrder.nativeOrder());
    int[] outArr = new int[TOTAL];

    Directory d;


    @Setup(Level.Trial)
    public void setup() throws IOException {

        var in = BaseSIMDIO.createRandomArray(TOTAL);

        for (int i = 0; i < TOTAL; i++) {
            memorySegment.set(ValueLayout.JAVA_INT, i * 4L, in[i]);
            byteBuffer.putInt(i * 4, in[i]);

        }


    }

    @Benchmark
    public void memorySegment2Vec512() throws IOException {
        BaseSIMDIO.memorySegment2vector(IntVector.SPECIES_512, memorySegment, TOTAL);
    }
    @Benchmark
    public void memorySegment2Vec256() throws IOException {
        BaseSIMDIO.memorySegment2vector(IntVector.SPECIES_256, memorySegment, TOTAL);
    }

    @Benchmark
    public void byteBuff2IntArr() throws IOException {
        BaseSIMDIO.bytebuf2array(byteBuffer, TOTAL);
    }

    @Benchmark
    public void intBuff2IntArr() throws IOException {
        BaseSIMDIO.intbuf2array(byteBuffer, TOTAL);
    }

    @Benchmark
    public void memorySegment2IntArr() throws IOException {
        BaseSIMDIO.memorySegment2intArr(memorySegment, TOTAL);
    }


}