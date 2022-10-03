package io.dashbase.local;

import io.dashbase.codec.benchmark.BaseSIMDIO;
import io.dashbase.codec.utils.ForUtil;
import io.dashbase.codec.utils.ForUtilV2;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ForUtilBenchmark {

    int SIZE = 1000;
    long[][] mockData;
    private ForUtil forUtil;
    final Directory d = new ByteBuffersDirectory();
    private ForUtilV2 forUtilV2;

    public long[][] mockData(int size, int maxBit) {
        long[][] out = new long[size][128];
        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 128; j++) {
                out[i][j] = random.nextInt(18);
            }
        }
        return out;
    }

    @Setup(Level.Trial)
    public void setup() throws IOException {
        mockData = mockData(SIZE, 24);

        forUtil = new ForUtil();
        forUtilV2 = new ForUtilV2();
    }

    @TearDown(Level.Invocation)
    public void tearDown() throws IOException {
        d.deleteFile("test.bin");
    }

    @Benchmark
    public void testForUtil() throws IOException {
        IndexOutput out = d.createOutput("test.bin", IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            out.writeByte((byte) 18);
            forUtil.encode(mockData[i], 18, out);
        }
        out.close();
    }

    @Benchmark
    public void testForUtil2() throws IOException {
        IndexOutput out = d.createOutput("test.bin", IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            out.writeByte((byte) 18);
            forUtilV2.encode(mockData[i], 18, out);
        }
        out.close();
    }
}
