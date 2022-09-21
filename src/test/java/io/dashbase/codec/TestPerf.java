package io.dashbase.codec;

import org.apache.lucene.store.*;
import org.apache.lucene.util.packed.PackedInts;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.dashbase.codec.ForUtil.BLOCK_SIZE;

public class TestPerf {

    ForUtil forUtil = new ForUtil();
    PForUtil pforUtil = new PForUtil(new ForUtil());


    public void decode(IndexInput in) throws IOException {
        final int bitsPerValue = in.readByte();
        final long currentFilePointer = in.getFilePointer();
        final long[] restored = new long[BLOCK_SIZE];

        long[] tmp = new long[BLOCK_SIZE / 2];

        forUtil.decode(bitsPerValue, in, restored);
    }

    public void encode(IndexOutput out, int[] values) throws IOException {
        long[] source = new long[BLOCK_SIZE];
        long or = 0;
        for (int j = 0; j < BLOCK_SIZE; ++j) {
            source[j] = values[j];
            or |= source[j];
        }
        final int bpv = PackedInts.bitsRequired(or);
        out.writeByte((byte) bpv);
        forUtil.encode(source, bpv, out);

    }

    public void decodePFor(IndexInput in) throws IOException {
        final long[] restored = new long[BLOCK_SIZE];
        pforUtil.decode(in, restored);
    }

    public void encodePFor(IndexOutput out, int[] values) throws IOException {
        long[] source = new long[BLOCK_SIZE];
        long or = 0;
        for (int j = 0; j < BLOCK_SIZE; ++j) {
            source[j] = values[j];
        }
        pforUtil.encode(source, out);
    }


    public void testFor(Directory d, int[] values, int cycle) throws IOException {
        IndexOutput out = d.createOutput("test.bin", IOContext.DEFAULT);
        for (int j = 0; j < cycle; j++) {
            encode(out, values);
        }
        out.close();

        // decode
        IndexInput in = d.openInput("test.bin", IOContext.READONCE);

        for (int j = 0; j < cycle; j++) {
            decode(in);
        }

        d.deleteFile("test.bin");
    }

    public void testPFor(Directory d, int[] values, int cycle) throws IOException {
        IndexOutput out = d.createOutput("test.bin", IOContext.DEFAULT);
        for (int j = 0; j < cycle; j++) {
            encodePFor(out, values);
        }
        out.close();

        // decode
        IndexInput in = d.openInput("test.bin", IOContext.READONCE);

        for (int j = 0; j < cycle; j++) {
            decodePFor(in);
        }

        d.deleteFile("test.bin");
    }

    @Test
    public void testEncodeDecode() throws IOException {
        final Directory d = new ByteBuffersDirectory();
        final long endPointer;

        var values = new int[128];
        for (int i = 0; i < 128; i++) {
            values[i] = i;
        }

        for (int i = 0; i < 1000; i++) {
            testFor(d, values, 10000);
            testPFor(d, values, 10000);

        }
    }

}
