package io.dashbase.codec.simd;

import io.dashbase.codec.CodeC;
import jdk.incubator.vector.*;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.packed.PackedInts;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;


public class BinaryPack extends AbsBinaryPack {

    // IntVector.SPECIES_128: 128 unsigned 32-bit integers per pack
    //      32 * 128-bit vector of 4 32-bit integers
    // IntVector.SPECIES_256: 256 unsigned 32-bit integers per pack
    //      32 * 256-bit vector of 8 32-bit integers
    // IntVector.SPECIES_512: 512 unsigned 32-bit integers per pack
    //      32 * 512-bit vector of 16 32-bit integers
    public BinaryPack(VectorSpecies<Integer> species) {
        super(species);
    }

    public int createVec(int[] arr, IntVector[] out) {
        return createVec(arr, out, 0);
    }

    // create 32 * IntVector array
    public int createVec(int[] arr, IntVector[] out, int offset) {
        var or = 0;
        for (int i = 0; i < 32; i++) {
            var v = IntVector.fromArray(SPECIES, arr, offset + i * VECTOR_LENGTH);
            out[i] = v;
            or |= v.reduceLanes(VectorOperators.OR);
        }

        return PackedInts.bitsRequired(or);
    }

    public IntVector[] createVec(int[] arr) {
        var size = arr.length / VECTOR_LENGTH;

        var out = new IntVector[size];
        for (int i = 0; i < size; i++) {
            out[i] = IntVector.fromArray(SPECIES, arr, i * VECTOR_LENGTH);
        }

        return out;
    }

    public IntVector[] packBlock(IntVector[] inVec, int bitSize) {
        IntVector[] out;
        switch (bitSize) {
            case 1 -> out = packBlock1(inVec);
            case 2 -> out = packBlock2(inVec);
            case 3 -> out = packBlock3(inVec);
            case 4 -> out = packBlock4(inVec);
            case 5 -> out = packBlock5(inVec);
            case 6 -> out = packBlock6(inVec);
            case 7 -> out = packBlock7(inVec);
            case 8 -> out = packBlock8(inVec);
            case 9 -> out = packBlock9(inVec);
            case 10 -> out = packBlock10(inVec);
            case 11 -> out = packBlock11(inVec);
            case 12 -> out = packBlock12(inVec);
            case 13 -> out = packBlock13(inVec);
            case 14 -> out = packBlock14(inVec);
            case 15 -> out = packBlock15(inVec);
            case 16 -> out = packBlock16(inVec);
            case 17 -> out = packBlock17(inVec);
            case 18 -> out = packBlock18(inVec);
            case 19 -> out = packBlock19(inVec);
            case 20 -> out = packBlock20(inVec);
            case 21 -> out = packBlock21(inVec);
            case 22 -> out = packBlock22(inVec);
            case 23 -> out = packBlock23(inVec);
            case 24 -> out = packBlock24(inVec);
            case 25 -> out = packBlock25(inVec);
            case 26 -> out = packBlock26(inVec);
            case 27 -> out = packBlock27(inVec);
            case 28 -> out = packBlock28(inVec);
            case 29 -> out = packBlock29(inVec);
            case 30 -> out = packBlock30(inVec);
            case 31 -> out = packBlock31(inVec);
            default -> out = packBlock32(inVec);
        }
        return out;
    }


    public IntVector[] unpackBlock(IntVector[] compassed, int bitSize) {
        IntVector[] out;
        switch (bitSize) {
            case 1 -> out = unpackBlock1(compassed);
            case 2 -> out = unpackBlock2(compassed);
            case 3 -> out = unpackBlock3(compassed);
            case 4 -> out = unpackBlock4(compassed);
            case 5 -> out = unpackBlock5(compassed);
            case 6 -> out = unpackBlock6(compassed);
            case 7 -> out = unpackBlock7(compassed);
            case 8 -> out = unpackBlock8(compassed);
            case 9 -> out = unpackBlock9(compassed);
            case 10 -> out = unpackBlock10(compassed);
            case 11 -> out = unpackBlock11(compassed);
            case 12 -> out = unpackBlock12(compassed);
            case 13 -> out = unpackBlock13(compassed);
            case 14 -> out = unpackBlock14(compassed);
            case 15 -> out = unpackBlock15(compassed);
            case 16 -> out = unpackBlock16(compassed);
            case 17 -> out = unpackBlock17(compassed);
            case 18 -> out = unpackBlock18(compassed);
            case 19 -> out = unpackBlock19(compassed);
            case 20 -> out = unpackBlock20(compassed);
            case 21 -> out = unpackBlock21(compassed);
            case 22 -> out = unpackBlock22(compassed);
            case 23 -> out = unpackBlock23(compassed);
            case 24 -> out = unpackBlock24(compassed);
            case 25 -> out = unpackBlock25(compassed);
            case 26 -> out = unpackBlock26(compassed);
            case 27 -> out = unpackBlock27(compassed);
            case 28 -> out = unpackBlock28(compassed);
            case 29 -> out = unpackBlock29(compassed);
            case 30 -> out = unpackBlock30(compassed);
            case 31 -> out = unpackBlock31(compassed);
            default -> out = unpackBlock32(compassed);
        }
        return out;
    }


    public int[] encodeI(int[] data) throws IOException {
        var inVec = new IntVector[32];
        var bit = createVec(data, inVec);
        var packedVec = packBlock(inVec, bit);

        var out = new int[bit * VECTOR_LENGTH];
        for (int i = 0; i < 7; i++) {
            var part = packedVec[i].toArray();
            System.arraycopy(part, 0, out, i * VECTOR_LENGTH, VECTOR_LENGTH);
        }
        return out;
    }

    public int[] encode(IntVector[] inVec, int bit) {
        var packedVec = encodeVec(inVec, bit);
        var out = new int[bit * VECTOR_LENGTH];
        for (int i = 0; i < bit; i++) {
            var part = packedVec[i].toArray();
            System.arraycopy(part, 0, out, i * VECTOR_LENGTH, VECTOR_LENGTH);
        }
        return out;
    }


    public IntVector[] encodeVec(IntVector[] inVec, int bit) {
        return packBlock(inVec, bit);
    }

//    public int[] encode(int[] data, IndexOutput compressed) throws IOException {
//        var inVec = new IntVector[32];
//        var bitSize = createVec(data, inVec);
//        var packedBlock = packBlock(inVec, bitSize);
//
//        var out = new int[32 * VECTOR_LENGTH];
//        var buf = ByteBuffer.allocate(bitSize * VECTOR_LENGTH * 4);
//        for (int i = 0; i < bitSize; i++) {
//            packedBlock[i].intoByteBuffer(buf, i * VECTOR_LENGTH * 4, ByteOrder.LITTLE_ENDIAN);
//        }
//
//        compressed.writeBytes(buf.array(), 0, buf.array().length);
//        return out;
//    }


    public int[] decode(IntVector[] data, int bitSize) {
        var packBlocked = unpackBlock(data, bitSize);
        var out = new int[32 * VECTOR_LENGTH];
        for (int i = 0; i < 32; i++) {
            var part = packBlocked[i].toArray();
            System.arraycopy(part, 0, out, i * VECTOR_LENGTH, VECTOR_LENGTH);

        }
        return out;
    }

    public int[] toArr(IntVector[] data, int size) {
        var out = new int[size * VECTOR_LENGTH];
        for (int i = 0; i < size; i++) {
            var part = data[i].toArray();
            System.arraycopy(part, 0, out, i * VECTOR_LENGTH, VECTOR_LENGTH);

        }

        return out;
    }


}
