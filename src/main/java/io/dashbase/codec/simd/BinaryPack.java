package io.dashbase.codec.simd;

import jdk.incubator.vector.*;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

import java.io.IOException;
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

    public IntVector[] createVec(int[] arr) {
        var out = new IntVector[32];
        for (int i = 0; i < 32; i++) {
            var v = IntVector.fromArray(SPECIES, arr, i * VECTOR_LENGTH);
            out[i] = v;

        }
        return out;
    }

    public IntVector[] packBlock(IntVector[] compassed, int bitSize) throws IOException {
        IntVector[] out;
        switch (bitSize) {
            case 1 -> out = packBlock1(compassed);
            case 2 -> out = packBlock2(compassed);
            case 3 -> out = packBlock3(compassed);
            case 4 -> out = packBlock4(compassed);
            case 5 -> out = packBlock5(compassed);
            case 6 -> out = packBlock6(compassed);
            case 7 -> out = packBlock7(compassed);
            case 8 -> out = packBlock8(compassed);
            case 9 -> out = packBlock9(compassed);
            case 10 -> out = packBlock10(compassed);
            case 11 -> out = packBlock11(compassed);
            case 12 -> out = packBlock12(compassed);
            case 13 -> out = packBlock13(compassed);
            case 14 -> out = packBlock14(compassed);
            case 15 -> out = packBlock15(compassed);
            case 16 -> out = packBlock16(compassed);
            case 17 -> out = packBlock17(compassed);
            case 18 -> out = packBlock18(compassed);
            case 19 -> out = packBlock19(compassed);
            case 20 -> out = packBlock20(compassed);
            case 21 -> out = packBlock21(compassed);
            case 22 -> out = packBlock22(compassed);
            case 23 -> out = packBlock23(compassed);
            case 24 -> out = packBlock24(compassed);
            case 25 -> out = packBlock25(compassed);
            case 26 -> out = packBlock26(compassed);
            case 27 -> out = packBlock27(compassed);
            case 28 -> out = packBlock28(compassed);
            case 29 -> out = packBlock29(compassed);
            case 30 -> out = packBlock30(compassed);
            case 31 -> out = packBlock31(compassed);
            case 32 -> out = packBlock32(compassed);
            default -> throw new IOException("bitSize must be in [1, 32], bitSize=" + bitSize);
        }
        return out;
    }


    public IntVector[] unpackBlock(IntVector[] compassed, int bitSize) throws IOException {
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
            case 32 -> out = unpackBlock32(compassed);
            default -> throw new IOException("bitSize must be in [1, 32], bitSize=" + bitSize);
        }
        return out;
    }


    public int[] encode(int[] data) throws IOException {
        var bit = 7;
        var packedBlock = packBlock(createVec(data), bit);

        var out = new int[bit * VECTOR_LENGTH];
        for (int i = 0; i < 7; i++) {
            var part = packedBlock[i].toArray();
            System.arraycopy(part, 0, out, i * VECTOR_LENGTH, VECTOR_LENGTH);
        }
        return out;
    }

    //    TODO
    public IntVector[] encodeV(int[] data) throws IOException {
        var bit = 7;
        return packBlock(createVec(data), bit);
    }

    public int[] encode(int[] data, IndexOutput compressed) throws IOException {
        var bitSize = 7;
        var packedBlock = packBlock(createVec(data), bitSize);

        var out = new int[32 * VECTOR_LENGTH];
        var buf = ByteBuffer.allocate(bitSize * VECTOR_LENGTH * 4);
        for (int i = 0; i < bitSize; i++) {
            packedBlock[i].intoByteBuffer(buf, i * VECTOR_LENGTH * 4, ByteOrder.LITTLE_ENDIAN);
        }

        compressed.writeBytes(buf.array(), 0, buf.array().length);
        return out;
    }

    //    TODO
    public int[] decode(IndexInput compressed, int bitSize) throws IOException {
        var compressedBlock = new IntVector[bitSize];

        var buf = new byte[bitSize * VECTOR_LENGTH * 4];
        for (int i = 0; i < bitSize; i++) {
            compressed.readBytes(buf, i * VECTOR_LENGTH * 4, VECTOR_LENGTH * 4);
            compressedBlock[i] = IntVector.fromByteArray(SPECIES, buf, i * VECTOR_LENGTH * 4, ByteOrder.LITTLE_ENDIAN);
        }
        System.out.println("dc:  " + Arrays.toString(toArr(compressedBlock, 7)));

        return decode(compressedBlock, bitSize);
    }

    public int[] decode(IntVector[] data, int bitSize) throws IOException {
        var packBlocked = unpackBlock(data, bitSize);
        var out = new int[32 * VECTOR_LENGTH];
        for (int i = 0; i < 32; i++) {
            var part = packBlocked[i].toArray();
            System.arraycopy(part, 0, out, i * VECTOR_LENGTH, VECTOR_LENGTH);

        }
        return out;
    }

    public int[] test(int[] data, int bitSize) throws IOException {
        var in = createVec(data);
        var compressed = packBlock(in, bitSize);
        var packBlocked = unpackBlock(compressed, bitSize);


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
