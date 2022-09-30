package io.dashbase.codec;

import jdk.incubator.vector.IntVector;

import static jdk.incubator.vector.VectorOperators.LSHL;
import static jdk.incubator.vector.VectorOperators.LSHR;

public class SIMDBinaryPacking {

    public static void fastpack17(IntVector[] in, int inPos,
                                  IntVector[] out, int outPos) {
        out[outPos + 0] = in[inPos + 0].or(in[inPos + 1].lanewise(LSHL, 17));
        out[outPos + 1] = in[inPos + 1].lanewise(LSHR, 17 - 2).or(in[inPos + 2].lanewise(LSHL, 2)).or(in[inPos + 3].lanewise(LSHL, 19));
        out[outPos + 2] = in[inPos + 3].lanewise(LSHR, 17 - 4).or(in[inPos + 4].lanewise(LSHL, 4)).or(in[inPos + 5].lanewise(LSHL, 21));
        out[outPos + 3] = in[inPos + 5].lanewise(LSHR, 17 - 6).or(in[inPos + 6].lanewise(LSHL, 6)).or(in[inPos + 7].lanewise(LSHL, 23));
        out[outPos + 4] = in[inPos + 7].lanewise(LSHR, 17 - 8).or(in[inPos + 8].lanewise(LSHL, 8)).or(in[inPos + 9].lanewise(LSHL, 25));
        out[outPos + 5] = in[inPos + 9].lanewise(LSHR, 17 - 10).or(in[inPos + 10].lanewise(LSHL, 10)).or(in[inPos + 11].lanewise(LSHL, 27));
        out[outPos + 6] = in[inPos + 11].lanewise(LSHR, 17 - 12).or(in[inPos + 12].lanewise(LSHL, 12)).or(in[inPos + 13].lanewise(LSHL, 29));
        out[outPos + 7] = in[inPos + 13].lanewise(LSHR, 17 - 14).or(in[inPos + 14].lanewise(LSHL, 14)).or(in[inPos + 15].lanewise(LSHL, 31));
        out[outPos + 8] = in[inPos + 15].lanewise(LSHR, 17 - 16).or(in[inPos + 16].lanewise(LSHL, 16));
        out[outPos + 9] = in[inPos + 16].lanewise(LSHR, 17 - 1).or(in[inPos + 17].lanewise(LSHL, 1)).or(in[inPos + 18].lanewise(LSHL, 18));
        out[outPos + 10] = in[inPos + 18].lanewise(LSHR, 17 - 3).or(in[inPos + 19].lanewise(LSHL, 3)).or(in[inPos + 20].lanewise(LSHL, 20));
        out[outPos + 11] = in[inPos + 20].lanewise(LSHR, 17 - 5).or(in[inPos + 21].lanewise(LSHL, 5)).or(in[inPos + 22].lanewise(LSHL, 22));
        out[outPos + 12] = in[inPos + 22].lanewise(LSHR, 17 - 7).or(in[inPos + 23].lanewise(LSHL, 7)).or(in[inPos + 24].lanewise(LSHL, 24));
        out[outPos + 13] = in[inPos + 24].lanewise(LSHR, 17 - 9).or(in[inPos + 25].lanewise(LSHL, 9)).or(in[inPos + 26].lanewise(LSHL, 26));
        out[outPos + 14] = in[inPos + 26].lanewise(LSHR, 17 - 11).or(in[inPos + 27].lanewise(LSHL, 11)).or(in[inPos + 28].lanewise(LSHL, 28));
        out[outPos + 15] = in[inPos + 28].lanewise(LSHR, 17 - 13).or(in[inPos + 29].lanewise(LSHL, 13)).or(in[inPos + 30].lanewise(LSHL, 30));
        out[outPos + 16] = in[inPos + 30].lanewise(LSHR, 17 - 15).or(in[inPos + 31].lanewise(LSHL, 15));

    }

    public static void fastunpack17(IntVector[] in, int inPos,
                                    IntVector[] out, int outPos) {
//        out[0 + outpos] = ((in[0 + inpos] >>> 0) & 131071);
//        out[1 + outpos] = (in[0 + inpos] >>> 17)
//            | ((in[1 + inpos] & 3) << (17 - 2));
//        out[2 + outpos] = ((in[1 + inpos] >>> 2) & 131071);
//        out[3 + outpos] = (in[1 + inpos] >>> 19)
//            | ((in[2 + inpos] & 15) << (17 - 4));
//        out[4 + outpos] = ((in[2 + inpos] >>> 4) & 131071);
//        out[5 + outpos] = (in[2 + inpos] >>> 21)
//            | ((in[3 + inpos] & 63) << (17 - 6));
//        out[6 + outpos] = ((in[3 + inpos] >>> 6) & 131071);
//        out[7 + outpos] = (in[3 + inpos] >>> 23)
//            | ((in[4 + inpos] & 255) << (17 - 8));
//        out[8 + outpos] = ((in[4 + inpos] >>> 8) & 131071);
//        out[9 + outpos] = (in[4 + inpos] >>> 25)
//            | ((in[5 + inpos] & 1023) << (17 - 10));
//        out[10 + outpos] = ((in[5 + inpos] >>> 10) & 131071);
//        out[11 + outpos] = (in[5 + inpos] >>> 27)
//            | ((in[6 + inpos] & 4095) << (17 - 12));
//        out[12 + outpos] = ((in[6 + inpos] >>> 12) & 131071);
//        out[13 + outpos] = (in[6 + inpos] >>> 29)
//            | ((in[7 + inpos] & 16383) << (17 - 14));
//        out[14 + outpos] = ((in[7 + inpos] >>> 14) & 131071);
//        out[15 + outpos] = (in[7 + inpos] >>> 31)
//            | ((in[8 + inpos] & 65535) << (17 - 16));
//        out[16 + outpos] = (in[8 + inpos] >>> 16)
//            | ((in[9 + inpos] & 1) << (17 - 1));
//        out[17 + outpos] = ((in[9 + inpos] >>> 1) & 131071);
//        out[18 + outpos] = (in[9 + inpos] >>> 18)
//            | ((in[10 + inpos] & 7) << (17 - 3));
//        out[19 + outpos] = ((in[10 + inpos] >>> 3) & 131071);
//        out[20 + outpos] = (in[10 + inpos] >>> 20)
//            | ((in[11 + inpos] & 31) << (17 - 5));
//        out[21 + outpos] = ((in[11 + inpos] >>> 5) & 131071);
//        out[22 + outpos] = (in[11 + inpos] >>> 22)
//            | ((in[12 + inpos] & 127) << (17 - 7));
//        out[23 + outpos] = ((in[12 + inpos] >>> 7) & 131071);
//        out[24 + outpos] = (in[12 + inpos] >>> 24)
//            | ((in[13 + inpos] & 511) << (17 - 9));
//        out[25 + outpos] = ((in[13 + inpos] >>> 9) & 131071);
//        out[26 + outpos] = (in[13 + inpos] >>> 26)
//            | ((in[14 + inpos] & 2047) << (17 - 11));
//        out[27 + outpos] = ((in[14 + inpos] >>> 11) & 131071);
//        out[28 + outpos] = (in[14 + inpos] >>> 28)
//            | ((in[15 + inpos] & 8191) << (17 - 13));
//        out[29 + outpos] = ((in[15 + inpos] >>> 13) & 131071);
//        out[30 + outpos] = (in[15 + inpos] >>> 30)
//            | ((in[16 + inpos] & 32767) << (17 - 15));
//        out[31 + outpos] = (in[16 + inpos] >>> 15);
//    }
        out[0 + outPos] = in[inPos + 0].lanewise(LSHR, 0);
        out[1 + outPos] = in[inPos + 0].lanewise(LSHR, 17).or(in[inPos + 1].and(3).lanewise(LSHL, 2));
        out[2 + outPos] = in[inPos + 1].lanewise(LSHR, 2);
        out[3 + outPos] = in[inPos + 1].lanewise(LSHR, 19).or(in[inPos + 2].and(15).lanewise(LSHL, 4));
        out[4 + outPos] = in[inPos + 2].lanewise(LSHR, 4);
        out[5 + outPos] = in[inPos + 2].lanewise(LSHR, 21).or(in[inPos + 3].and(63).lanewise(LSHL, 6));
        out[6 + outPos] = in[inPos + 3].lanewise(LSHR, 6);
        out[7 + outPos] = in[inPos + 3].lanewise(LSHR, 23).or(in[inPos + 4].and(255).lanewise(LSHL, 8));
        out[8 + outPos] = in[inPos + 4].lanewise(LSHR, 8);
        out[9 + outPos] = in[inPos + 4].lanewise(LSHR, 25).or(in[inPos + 5].and(1023).lanewise(LSHL, 10));
        out[10 + outPos] = in[inPos + 5].lanewise(LSHR, 10);
        out[11 + outPos] = in[inPos + 5].lanewise(LSHR, 27).or(in[inPos + 6].and(4095).lanewise(LSHL, 12));
        out[12 + outPos] = in[inPos + 6].lanewise(LSHR, 12);
        out[13 + outPos] = in[inPos + 6].lanewise(LSHR, 29).or(in[inPos + 7].and(16383).lanewise(LSHL, 14));
        out[14 + outPos] = in[inPos + 7].lanewise(LSHR, 14);
        out[15 + outPos] = in[inPos + 7].lanewise(LSHR, 31).or(in[inPos + 8].and(65535).lanewise(LSHL, 16));
        out[16 + outPos] = in[inPos + 8].lanewise(LSHR, 16).or(in[inPos + 9].and(1).lanewise(LSHL, 1));
        out[17 + outPos] = in[inPos + 9].lanewise(LSHR, 1);
        out[18 + outPos] = in[inPos + 9].lanewise(LSHR, 18).or(in[inPos + 10].and(7).lanewise(LSHL, 3));
        out[19 + outPos] = in[inPos + 10].lanewise(LSHR, 3);
        out[20 + outPos] = in[inPos + 10].lanewise(LSHR, 20).or(in[inPos + 11].and(31).lanewise(LSHL, 5));
        out[21 + outPos] = in[inPos + 11].lanewise(LSHR, 5);
        out[22 + outPos] = in[inPos + 11].lanewise(LSHR, 22).or(in[inPos + 12].and(127).lanewise(LSHL, 7));
        out[23 + outPos] = in[inPos + 12].lanewise(LSHR, 7);
        out[24 + outPos] = in[inPos + 12].lanewise(LSHR, 24).or(in[inPos + 13].and(511).lanewise(LSHL, 9));
        out[25 + outPos] = in[inPos + 13].lanewise(LSHR, 9);

        out[26 + outPos] = in[inPos + 13].lanewise(LSHR, 26).or(in[inPos + 14].and(2047).lanewise(LSHL, 11));
        out[27 + outPos] = in[inPos + 14].lanewise(LSHR, 11);
        out[28 + outPos] = in[inPos + 14].lanewise(LSHR, 28).or(in[inPos + 15].and(8191).lanewise(LSHL, 13));
        out[29 + outPos] = in[inPos + 15].lanewise(LSHR, 13);
        out[30 + outPos] = in[inPos + 15].lanewise(LSHR, 30).or(in[inPos + 16].and(32767).lanewise(LSHL, 15));
        out[31 + outPos] = in[inPos + 16].lanewise(LSHR, 15);

    }

}
