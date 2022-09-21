package io.dashbase.codec;

import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.packed.PackedInts;

import java.io.IOException;

import static io.dashbase.codec.ForUtil.BLOCK_SIZE;

public class Lucene {
    final ForUtil forUtil = new ForUtil();

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

    public int[] test(int[] values, IndexOutput out, IndexInput in) throws Exception {

        encode(out, values);
        out.close();

        decode(in);
        return values;
    }
}
