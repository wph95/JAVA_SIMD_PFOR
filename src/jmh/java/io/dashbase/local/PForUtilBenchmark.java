package io.dashbase.local;

import io.dashbase.codec.utils.*;
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
public class PForUtilBenchmark {

    int SIZE = 1000;
    long[][] mockData;
    private PForUtil pForUtil;
    final Directory d = new ByteBuffersDirectory();
    private PForUtilV2 pForUtilV2;
    long[] tmpInput = new long[128];
    long[] outArr= new long[128];

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

        pForUtil = new PForUtil(new ForUtil());
        pForUtilV2 = new PForUtilV2();

        encode(pForUtil, "pForUtil.bin");
        encode(pForUtilV2, "pForUtilV2.bin");

    }

    @TearDown(Level.Invocation)
    public void tearDown() throws IOException {
        try {
            d.deleteFile("test.bin");
        } catch (Exception e) {
            // ignore
        }
    }

    @Benchmark
    public void testForUtil() throws IOException {
        encode(pForUtil, "test.bin");
    }

    @Benchmark
    public void testForUtil2() throws IOException {
        encode(pForUtilV2, "test.bin");
    }

    @Benchmark
    public void testForUtilDecode() throws IOException {
        decode(pForUtil, "pForUtil.bin");
    }

    @Benchmark
    public void testForUtilDecodeV2() throws IOException {
        decode(pForUtilV2, "pForUtilV2.bin");
    }



    public void decode(BasePForUtil util, String fileName) throws IOException {
        var in = d.openInput(fileName, IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            util.decode(in, outArr);
        }
        in.close();
    }

    public void encode(BasePForUtil base, String filename) throws IOException {
        IndexOutput out = d.createOutput(filename, IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(mockData[i], 0, tmpInput, 0, 128);
            base.encode(tmpInput, out);
        }
        out.close();
    }
}
