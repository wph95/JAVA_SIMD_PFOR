package io.dashbase.codec;

import io.dashbase.codec.io.MemorySegmentInput;
import io.dashbase.codec.io.MemorySegmentOutput;
import io.dashbase.codec.utils.ForUtil;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.packed.PackedInts;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

import static io.dashbase.codec.utils.ForUtil.BLOCK_SIZE;

public class Lucene implements CodeC {
    final ForUtil forUtil = new ForUtil();

    public long[] decode(IndexInput in) throws IOException {
        final int bitsPerValue = (int) in.readLong();
        final long[] restored = new long[BLOCK_SIZE];


        forUtil.decode(bitsPerValue, in, restored);
        return restored;
    }

    public void encode(IndexOutput out, long[] values) throws IOException {
        long[] source = new long[BLOCK_SIZE];
        long or = 0;
        for (int j = 0; j < BLOCK_SIZE; ++j) {
            source[j] = values[j];
            or |= source[j];
        }
        final int bpv = PackedInts.bitsRequired(or);
        out.writeLong(bpv);
        forUtil.encode(source, bpv, out);

    }

    public void encode(IndexOutput out, int[] values) throws IOException {
        long[] source = new long[BLOCK_SIZE];
        long or = 0;
        for (int j = 0; j < BLOCK_SIZE; ++j) {
            source[j] = values[j];
            or |= source[j];
        }
        final int bpv = PackedInts.bitsRequired(or);
        out.writeLong(bpv);
        forUtil.encode(source, bpv, out);

    }


    @Override
    public long encode(MemorySegment segment, int[] values) throws IOException {
        return 0;
    }

    @Override
    public long encode(MemorySegment segment, long[] values) throws IOException {
        var blockSize = values.length / 128;
        var output = new MemorySegmentOutput("test", "test_name", segment);
        output.writeLong(blockSize);
        for (int i = 0; i < blockSize; i++) {
            var d= new long[128];
            System.arraycopy(values, i * 128, d, 0, 128);

            encode(output, d);
        }

        return output.getFilePointer();
    }

    @Override
    public long[] decode(MemorySegment segment) throws IOException {
        var input = new MemorySegmentInput("test", segment);
        var blockSize = input.readLong();
        var output = new long[(int) blockSize * 128];
        for (int i = 0; i < blockSize; i++) {
            final int bitsPerValue = (int) input.readLong();
            final long[] decoded = new long[BLOCK_SIZE];
            forUtil.decode(bitsPerValue, input, decoded);
            System.arraycopy(decoded, 0, output, i * 128, 128);
        }
        return output;
    }

    @Override
    public int[] decodeInt(MemorySegment segment) throws IOException {
        return new int[0];
    }
}
