package io.dashbase.codec;

import io.dashbase.codec.simd.BinaryPack;
import jdk.incubator.vector.IntVector;
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

        for (int i = 0; i < 100; i++) {
            testFor(d, values, 10000);
            testPFor(d, values, 10000);

        }
    }

    @Test
    public void testSIMD() throws IOException {
        var dataI = new int[512];
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 8; j++) {
                dataI[i * 8 + j] += 100 + ((i + j) % 4);
            }
        }

        for (int i = 0; i < 5000_000; i++) {
            var codec = new BinaryPack(IntVector.SPECIES_512);
            codec.test(dataI, 7);
        }

    }

}
