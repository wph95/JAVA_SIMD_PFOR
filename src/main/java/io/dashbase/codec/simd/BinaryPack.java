package io.dashbase.codec.simd;

import jdk.incubator.vector.*;

import static jdk.incubator.vector.VectorOperators.*;

public class BinaryPack extends AbsBinaryPack {


    public IntVector[] R;
    public IntVector[] G;

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



    public int[] test(int[] data) {
        var in = createVec(data);
        var compressed = pack7(in);
        var packed = unpack7(compressed);


        var out = new int[32 * VECTOR_LENGTH];
        for (int i = 0; i < 32; i++) {
            var part = packed[i].toArray();
            System.arraycopy(part, 0, out, i * VECTOR_LENGTH, 8);

        }
        return out;
    }
}
