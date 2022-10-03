package io.dashbase.codec.utils;

import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PerfForUtilTest {
    int SIZE = 100000;
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


    public void encodeForUtil() throws IOException {
        IndexOutput out = d.createOutput("test.bin", IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            out.writeByte((byte) 18);
            forUtil.encode(mockData[i], 18, out);
        }
        out.close();
        d.deleteFile("test.bin");
    }

    public void encodeForUtilV2() throws IOException {
        var out = d.createOutput("test.bin", IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            out.writeByte((byte) 18);
            forUtilV2.encode(mockData[i], 18, out);
        }
        out.close();
        d.deleteFile("test.bin");
    }



    @Test
    public void testPerf() throws IOException {
        mockData = mockData(SIZE, 24);

        forUtil = new ForUtil();
        forUtilV2 = new ForUtilV2();


        for (int j = 0; j < 200; j++) {
            encodeForUtil();
            encodeForUtilV2();

        }


    }
}
