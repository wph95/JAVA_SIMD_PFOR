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
    long[] tmpInput = new long[128];
    long[] outArr= new long[128];
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

    public void decode(PForUtil util, String fileName) throws IOException {
        var in = d.openInput(fileName, IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            util.decode(in, outArr);
        }
        in.close();
    }

    public void encode(PForUtil base, String filename) throws IOException {
        IndexOutput out = d.createOutput(filename, IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(mockData[i], 0, tmpInput, 0, 128);
            base.encode(tmpInput, out);
        }
        out.close();
    }

    public void decodeV2(PForUtilV2 util, String fileName) throws IOException {
        var in = d.openInput(fileName, IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            util.decode(in, outArr);
        }
        in.close();
    }

    public void encodeV2(PForUtilV2 base, String filename) throws IOException {
        IndexOutput out = d.createOutput(filename, IOContext.DEFAULT);
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(mockData[i], 0, tmpInput, 0, 128);
            base.encode(tmpInput, out);
        }
        out.close();
    }

    public void encode() throws IOException {
        encode(pForUtil, "pForUtil.bin");
        encodeV2(pForUtilV2, "pForUtilV2.bin");
    }

    public void decode() throws IOException {
        decode(pForUtil, "pForUtil.bin");
        decodeV2(pForUtilV2, "pForUtilV2.bin");
    }

    @Test
    public void testPerf() throws IOException {
        mockData = createMockData(SIZE, 24);

        for (int j = 0; j < 1000; j++) {

            encode();
            decode();

            d.deleteFile("pForUtil.bin");
            d.deleteFile("pForUtilV2.bin");

        }


    }
}
