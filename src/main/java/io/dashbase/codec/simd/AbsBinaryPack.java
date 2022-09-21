package io.dashbase.codec.simd;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

import static jdk.incubator.vector.VectorOperators.*;
import static jdk.incubator.vector.VectorOperators.LSHR;

public class AbsBinaryPack extends AbsSIMD {
    public AbsBinaryPack(VectorSpecies<Integer> species) {
        super(species);
    }

    public IntVector[] _pack2(IntVector[] input) {
        var out = new IntVector[2];

        out[0] = input[0];
        for (int i = 0; i < 15; i++) {
            out[0] = out[0].or(input[i + 1].lanewise(LSHL, i * 2 + 2));
        }
        out[1] = input[16];
        for (int i = 0; i < 15; i++) {
            out[1] = out[1].or(input[i + 17].lanewise(LSHL, i * 2 + 2));
        }


        return out;
    }


    public IntVector[] _unpack2(IntVector[] in) {
        var mask = IntVector.broadcast(SPECIES, 3);
        var out = new IntVector[32];


        for (int j = 0; j < 2; j++) {
            out[j * 16] = mask.lanewise(AND, in[j]);
            for (int i = 1; i < 16; i++) {
                out[i + j * 16] = mask.and(in[j].lanewise(LSHR, 2 * i));
            }
        }

        return out;
    }

    public IntVector[] pack7(IntVector[] input) {
        var in = 0;
        var compressed = 0;
        IN = input;
        OUT = new IntVector[7];
        IntVector w0, w1, tmp;

        w0 = _mm256_lddqu_si256(in + 0);
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 1), 7));
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 2), 14));
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 3), 21));
        tmp = _mm256_lddqu_si256(in + 4);
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(tmp, 28));
        w1 = _mm256_srli_epi32(tmp, 4);
        _mm256_storeu_si256(compressed + 0, w0);
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 5), 3));
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 6), 10));
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 7), 17));
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 8), 24));
        tmp = _mm256_lddqu_si256(in + 9);
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(tmp, 31));
        w0 = _mm256_srli_epi32(tmp, 1);
        _mm256_storeu_si256(compressed + 1, w1);
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 10), 6));
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 11), 13));
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 12), 20));
        tmp = _mm256_lddqu_si256(in + 13);
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(tmp, 27));
        w1 = _mm256_srli_epi32(tmp, 5);
        _mm256_storeu_si256(compressed + 2, w0);
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 14), 2));
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 15), 9));
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 16), 16));
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 17), 23));
        tmp = _mm256_lddqu_si256(in + 18);
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(tmp, 30));
        w0 = _mm256_srli_epi32(tmp, 2);
        _mm256_storeu_si256(compressed + 3, w1);
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 19), 5));
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 20), 12));
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 21), 19));
        tmp = _mm256_lddqu_si256(in + 22);
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(tmp, 26));
        w1 = _mm256_srli_epi32(tmp, 6);
        _mm256_storeu_si256(compressed + 4, w0);
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 23), 1));
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 24), 8));
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 25), 15));
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(_mm256_lddqu_si256(in + 26), 22));
        tmp = _mm256_lddqu_si256(in + 27);
        w1 = _mm256_or_si256(w1, _mm256_slli_epi32(tmp, 29));
        w0 = _mm256_srli_epi32(tmp, 3);
        _mm256_storeu_si256(compressed + 5, w1);
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 28), 4));
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 29), 11));
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 30), 18));
        w0 = _mm256_or_si256(w0, _mm256_slli_epi32(_mm256_lddqu_si256(in + 31), 25));
        _mm256_storeu_si256(compressed + 6, w0);
        return OUT;
    }

    /* we packed 256 7-bit values, touching 7 256-bit words, using 112 bytes */
    public IntVector[] unpack7(IntVector[] input) {
        /* we are going to access  7 256-bit words */
        IntVector w0, w1;
        var out = 0;
        var compressed = 0;
        IN = input;
        OUT = new IntVector[32];

        IntVector mask = _mm256_set1_epi32(127);
        w0 = _mm256_lddqu_si256(compressed);
        _mm256_storeu_si256(out + 0, _mm256_and_si256(mask, w0));
        _mm256_storeu_si256(out + 1,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 7)));
        _mm256_storeu_si256(out + 2,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 14)));
        _mm256_storeu_si256(out + 3,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 21)));
        w1 = _mm256_lddqu_si256(compressed + 1);
        _mm256_storeu_si256(
            out + 4,
            _mm256_and_si256(mask, _mm256_or_si256(_mm256_srli_epi32(w0, 28),
                                                   _mm256_slli_epi32(w1, 4))));
        _mm256_storeu_si256(out + 5,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 3)));
        _mm256_storeu_si256(out + 6,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 10)));
        _mm256_storeu_si256(out + 7,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 17)));
        _mm256_storeu_si256(out + 8,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 24)));
        w0 = _mm256_lddqu_si256(compressed + 2);
        _mm256_storeu_si256(
            out + 9,
            _mm256_and_si256(mask, _mm256_or_si256(_mm256_srli_epi32(w1, 31),
                                                   _mm256_slli_epi32(w0, 1))));
        _mm256_storeu_si256(out + 10,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 6)));
        _mm256_storeu_si256(out + 11,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 13)));
        _mm256_storeu_si256(out + 12,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 20)));
        w1 = _mm256_lddqu_si256(compressed + 3);
        _mm256_storeu_si256(
            out + 13,
            _mm256_and_si256(mask, _mm256_or_si256(_mm256_srli_epi32(w0, 27),
                                                   _mm256_slli_epi32(w1, 5))));
        _mm256_storeu_si256(out + 14,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 2)));
        _mm256_storeu_si256(out + 15,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 9)));
        _mm256_storeu_si256(out + 16,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 16)));
        _mm256_storeu_si256(out + 17,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 23)));
        w0 = _mm256_lddqu_si256(compressed + 4);
        _mm256_storeu_si256(
            out + 18,
            _mm256_and_si256(mask, _mm256_or_si256(_mm256_srli_epi32(w1, 30),
                                                   _mm256_slli_epi32(w0, 2))));
        _mm256_storeu_si256(out + 19,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 5)));
        _mm256_storeu_si256(out + 20,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 12)));
        _mm256_storeu_si256(out + 21,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 19)));
        w1 = _mm256_lddqu_si256(compressed + 5);
        _mm256_storeu_si256(
            out + 22,
            _mm256_and_si256(mask, _mm256_or_si256(_mm256_srli_epi32(w0, 26),
                                                   _mm256_slli_epi32(w1, 6))));
        _mm256_storeu_si256(out + 23,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 1)));
        _mm256_storeu_si256(out + 24,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 8)));
        _mm256_storeu_si256(out + 25,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 15)));
        _mm256_storeu_si256(out + 26,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w1, 22)));
        w0 = _mm256_lddqu_si256(compressed + 6);
        _mm256_storeu_si256(
            out + 27,
            _mm256_and_si256(mask, _mm256_or_si256(_mm256_srli_epi32(w1, 29),
                                                   _mm256_slli_epi32(w0, 3))));
        _mm256_storeu_si256(out + 28,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 4)));
        _mm256_storeu_si256(out + 29,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 11)));
        _mm256_storeu_si256(out + 30,
                            _mm256_and_si256(mask, _mm256_srli_epi32(w0, 18)));
        _mm256_storeu_si256(out + 31, _mm256_srli_epi32(w0, 25));
        return OUT;
    }

}
