package io.dashbase.local;

import jdk.incubator.vector.IntVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
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
    final static int SIZE = 1000;
    int[] intArr = new int[32 * SIZE];
    final static MemorySession session = MemorySession.openConfined();
    MemorySegment memorySegment = MemorySegment.allocateNative(32 * SIZE * 8, session);
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(32 * SIZE * 8).order(ByteOrder.nativeOrder());
    IntVector[] outVec = new IntVector[1000];
    int[] outArr = new int[32 * SIZE];

    Directory d;

    @Setup
    public void setup() throws IOException {
        d = new MMapDirectory(Path.of("/tmp"));

        var out = d.createOutput("test_lucene", IOContext.DEFAULT);
        for (int i = 0; i < 32 * SIZE; i++) {
            intArr[i] = i;
            memorySegment.set(ValueLayout.JAVA_INT, i * 8, i);
            byteBuffer.putInt(i * 8, i);
            out.writeInt(i);
        }


        out.close();


    }


//    @BaseBenchmark
//    public void intArr2Vec() throws IOException {
//        for (int i = 0; i < 1000; i++) {
//            outVec[i] = IntVector.fromArray(IntVector.SPECIES_512, intArr, i * 32);
//        }
//    }
//
//    @BaseBenchmark
//    public void memorySegment2Vec() throws IOException {
//        for (int i = 0; i < 1000; i++) {
//            outVec[i] = IntVector.fromMemorySegment(IntVector.SPECIES_512, memorySegment, i * 32 * 8, ByteOrder.LITTLE_ENDIAN);
//        }
//    }

    @Benchmark
    public void byteBuff2IntArr() throws IOException {
        var in = d.openInput("test_lucene", IOContext.DEFAULT);
        in.readInts(outArr, 0, SIZE * 32);
    }


}