package io.dashbase.codec.simd;

import io.dashbase.codec.CodeC;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteOrder;

public class BinaryPackCodeC extends BinaryPack implements CodeC {
    int BLOCK_SIZE = 32 * VECTOR_LENGTH;

    public BinaryPackCodeC(VectorSpecies<Integer> species) {
        super(species);
    }

    @Override
    public long encode(MemorySegment compressed, int[] values) throws IOException {
        var inpos = 0;
        var outpos = 0;
        var inlength = values.length;
        var out = new IntVector[32];
        compressed.set(ValueLayout.JAVA_INT, outpos, inlength / BLOCK_SIZE);
        outpos += 4;
        for (int i = 0; i < inlength / BLOCK_SIZE; i++) {

            // get and set bitSize
            var bitSize = createVec(values, out, inpos);
            compressed.set(ValueLayout.JAVA_INT, outpos, bitSize);
            outpos += 4;

            // save compressed
            var blockCompressed = packBlock(out, bitSize);
            for (int j = 0; j < bitSize; j++) {
                blockCompressed[j].intoMemorySegment(compressed, outpos, ByteOrder.nativeOrder());
                outpos += VECTOR_LENGTH * 4;
            }
            inpos += BLOCK_SIZE;
        }

        return outpos;
    }

    @Override
    public long encode(MemorySegment segment, long[] values) throws IOException {
        return 0;
    }

    @Override
    public long[] decode(MemorySegment segment) throws IOException {
        return new long[0];
    }

    @Override
    public int[] decodeInt(MemorySegment segment) throws IOException {
        var inpos = 0;
        var blockCount = segment.get(ValueLayout.JAVA_INT, inpos);
        inpos += 4;
        var out = new int[blockCount * BLOCK_SIZE];
        for (int i = 0; i < blockCount; i++) {
            var bitSize = segment.get(ValueLayout.JAVA_INT, inpos);
            inpos += 4;

            var inVec = new IntVector[32];
            for (int j = 0; j < bitSize; j++) {
                inVec[j] = IntVector.fromMemorySegment(SPECIES, segment, inpos, ByteOrder.nativeOrder());
                inpos += VECTOR_LENGTH * 4;
            }
            var blockOut = unpackBlock(inVec, bitSize);
            for (int j = 0; j < 32; j++) {
                blockOut[j].intoArray(out, i * BLOCK_SIZE + j * VECTOR_LENGTH);
            }

        }
        return out;
    }
}
