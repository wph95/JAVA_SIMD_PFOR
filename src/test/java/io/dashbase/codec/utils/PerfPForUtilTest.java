package io.dashbase.codec.utils;

import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PerfPForUtilTest {
    int SIZE = 10000;
    long[][] mockData;
    private PForUtil pForUtil = new PForUtil(new ForUtil());
    private PForUtilV2 pForUtilV2 = new PForUtilV2();
    final Directory d = new ByteBuffersDirectory();

    public long[][] createMockData(int size, int maxBit) {
        long[][] out = new long[size][128];
        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 128; j++) {
                out[i][j] = random.nextInt(2<<maxBit);
            }
        }
        return out;
    }


    public void encodeForUtil() throws IOException {
        IndexOutput out = d.createOutput("test.bin", IOContext.DEFAULT);
        var input = new long[128];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < 128; j++) {
                input[j] = mockData[i][j];
            }
            pForUtil.encode(input,  out);
        }
        out.close();
        d.deleteFile("test.bin");
    }

    public void encodeForUtilV2() throws IOException {
        var out = d.createOutput("test.bin", IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            pForUtilV2.encode(mockData[i],  out);
        }
        out.close();
        d.deleteFile("test.bin");
    }



    @Test
    public void testPerf() throws IOException {
        mockData = createMockData(SIZE, 24);

        for (int j = 0; j < 1000; j++) {
            encodeForUtil();
            encodeForUtilV2();

        }


    }
}
