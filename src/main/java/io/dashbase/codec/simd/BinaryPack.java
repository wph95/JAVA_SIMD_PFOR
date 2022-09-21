package io.dashbase.codec.simd;

import jdk.incubator.vector.*;

import java.io.IOException;


public class BinaryPack extends AbsBinaryPack {


    public BinaryPack(VectorSpecies<Integer> species) {
        super(species);
    }

    public IntVector[] createVec(int[] arr) {
        var out = new IntVector[32];
        for (int i = 0; i < 32; i++) {
            var v = IntVector.fromArray(IntVector.SPECIES_256, arr, i * VECTOR_LENGTH);
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


    public int[] test(int[] data, int size) throws IOException {
        var in = createVec(data);
        var compressed = packBlock(in, size);
        var packBlocked = unpackBlock(compressed, size);


        var out = new int[32 * VECTOR_LENGTH];
        for (int i = 0; i < 32; i++) {
            var part = packBlocked[i].toArray();
            System.arraycopy(part, 0, out, i * VECTOR_LENGTH, 8);

        }
        return out;
    }
}
