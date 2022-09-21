package io.dashbase.codec.simd;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

import static jdk.incubator.vector.VectorOperators.LSHL;
import static jdk.incubator.vector.VectorOperators.LSHR;

public class AbsSIMD {
    public final VectorSpecies<Integer> SPECIES;
    public final int VECTOR_LENGTH;

    public IntVector[] IN;
    public IntVector[] OUT;

    public AbsSIMD(VectorSpecies<Integer> species) {
        SPECIES = species;
        VECTOR_LENGTH = species.length();
    }

    public IntVector _mm256_lddqu_si256(int pos) {
        return IN[pos];
    }

    public IntVector _mm256_or_si256(IntVector w0, IntVector w1) {
        return w0.or(w1);
    }

    public IntVector _mm256_slli_epi32(IntVector w0, int i) {
        return w0.lanewise(LSHL, i);
    }

    public IntVector _mm256_srli_epi32(IntVector w0, int i) {
        return w0.lanewise(LSHR, i);
    }

    public void _mm256_storeu_si256(int pos, IntVector w1) {
        OUT[pos] = w1;
    }

    public IntVector _mm256_and_si256(IntVector w0, IntVector w1) {
        return w0.and(w1);
    }

    public IntVector _mm256_set1_epi32(int i) {
        return IntVector.broadcast(SPECIES, i);
    }

}
