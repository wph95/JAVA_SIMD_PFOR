package io.dashbase.codec;

import me.lemire.integercompression.Composition;
import me.lemire.integercompression.VariableByte;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

public class BinaryPacking {

    public static void fastpack17(final int[] in, int inpos,
                                  final int[] out, int outpos) {
        out[0 + outpos] = (in[0 + inpos] & 131071)
            | ((in[1 + inpos]) << 17);
        out[1 + outpos] = ((in[1 + inpos] & 131071) >>> (17 - 2))
            | ((in[2 + inpos] & 131071) << 2)
            | ((in[3 + inpos]) << 19);
        out[2 + outpos] = ((in[3 + inpos] & 131071) >>> (17 - 4))
            | ((in[4 + inpos] & 131071) << 4)
            | ((in[5 + inpos]) << 21);
        out[3 + outpos] = ((in[5 + inpos] & 131071) >>> (17 - 6))
            | ((in[6 + inpos] & 131071) << 6)
            | ((in[7 + inpos]) << 23);
        out[4 + outpos] = ((in[7 + inpos] & 131071) >>> (17 - 8))
            | ((in[8 + inpos] & 131071) << 8)
            | ((in[9 + inpos]) << 25);
        out[5 + outpos] = ((in[9 + inpos] & 131071) >>> (17 - 10))
            | ((in[10 + inpos] & 131071) << 10)
            | ((in[11 + inpos]) << 27);
        out[6 + outpos] = ((in[11 + inpos] & 131071) >>> (17 - 12))
            | ((in[12 + inpos] & 131071) << 12)
            | ((in[13 + inpos]) << 29);
        out[7 + outpos] = ((in[13 + inpos] & 131071) >>> (17 - 14))
            | ((in[14 + inpos] & 131071) << 14)
            | ((in[15 + inpos]) << 31);
        out[8 + outpos] = ((in[15 + inpos] & 131071) >>> (17 - 16))
            | ((in[16 + inpos]) << 16);
        out[9 + outpos] = ((in[16 + inpos] & 131071) >>> (17 - 1))
            | ((in[17 + inpos] & 131071) << 1)
            | ((in[18 + inpos]) << 18);
        out[10 + outpos] = ((in[18 + inpos] & 131071) >>> (17 - 3))
            | ((in[19 + inpos] & 131071) << 3)
            | ((in[20 + inpos]) << 20);
        out[11 + outpos] = ((in[20 + inpos] & 131071) >>> (17 - 5))
            | ((in[21 + inpos] & 131071) << 5)
            | ((in[22 + inpos]) << 22);
        out[12 + outpos] = ((in[22 + inpos] & 131071) >>> (17 - 7))
            | ((in[23 + inpos] & 131071) << 7)
            | ((in[24 + inpos]) << 24);
        out[13 + outpos] = ((in[24 + inpos] & 131071) >>> (17 - 9))
            | ((in[25 + inpos] & 131071) << 9)
            | ((in[26 + inpos]) << 26);
        out[14 + outpos] = ((in[26 + inpos] & 131071) >>> (17 - 11))
            | ((in[27 + inpos] & 131071) << 11)
            | ((in[28 + inpos]) << 28);
        out[15 + outpos] = ((in[28 + inpos] & 131071) >>> (17 - 13))
            | ((in[29 + inpos] & 131071) << 13)
            | ((in[30 + inpos]) << 30);
        out[16 + outpos] = ((in[30 + inpos] & 131071) >>> (17 - 15))
            | ((in[31 + inpos]) << 15);
    }

    public static void fastunpack17(final int[] in, int inpos,
                                       final int[] out, int outpos) {
        out[0 + outpos] = ((in[0 + inpos] >>> 0) & 131071);
        out[1 + outpos] = (in[0 + inpos] >>> 17)
            | ((in[1 + inpos] & 3) << (17 - 2));
        out[2 + outpos] = ((in[1 + inpos] >>> 2) & 131071);
        out[3 + outpos] = (in[1 + inpos] >>> 19)
            | ((in[2 + inpos] & 15) << (17 - 4));
        out[4 + outpos] = ((in[2 + inpos] >>> 4) & 131071);
        out[5 + outpos] = (in[2 + inpos] >>> 21)
            | ((in[3 + inpos] & 63) << (17 - 6));
        out[6 + outpos] = ((in[3 + inpos] >>> 6) & 131071);
        out[7 + outpos] = (in[3 + inpos] >>> 23)
            | ((in[4 + inpos] & 255) << (17 - 8));
        out[8 + outpos] = ((in[4 + inpos] >>> 8) & 131071);
        out[9 + outpos] = (in[4 + inpos] >>> 25)
            | ((in[5 + inpos] & 1023) << (17 - 10));
        out[10 + outpos] = ((in[5 + inpos] >>> 10) & 131071);
        out[11 + outpos] = (in[5 + inpos] >>> 27)
            | ((in[6 + inpos] & 4095) << (17 - 12));
        out[12 + outpos] = ((in[6 + inpos] >>> 12) & 131071);
        out[13 + outpos] = (in[6 + inpos] >>> 29)
            | ((in[7 + inpos] & 16383) << (17 - 14));
        out[14 + outpos] = ((in[7 + inpos] >>> 14) & 131071);
        out[15 + outpos] = (in[7 + inpos] >>> 31)
            | ((in[8 + inpos] & 65535) << (17 - 16));
        out[16 + outpos] = (in[8 + inpos] >>> 16)
            | ((in[9 + inpos] & 1) << (17 - 1));
        out[17 + outpos] = ((in[9 + inpos] >>> 1) & 131071);
        out[18 + outpos] = (in[9 + inpos] >>> 18)
            | ((in[10 + inpos] & 7) << (17 - 3));
        out[19 + outpos] = ((in[10 + inpos] >>> 3) & 131071);
        out[20 + outpos] = (in[10 + inpos] >>> 20)
            | ((in[11 + inpos] & 31) << (17 - 5));
        out[21 + outpos] = ((in[11 + inpos] >>> 5) & 131071);
        out[22 + outpos] = (in[11 + inpos] >>> 22)
            | ((in[12 + inpos] & 127) << (17 - 7));
        out[23 + outpos] = ((in[12 + inpos] >>> 7) & 131071);
        out[24 + outpos] = (in[12 + inpos] >>> 24)
            | ((in[13 + inpos] & 511) << (17 - 9));
        out[25 + outpos] = ((in[13 + inpos] >>> 9) & 131071);
        out[26 + outpos] = (in[13 + inpos] >>> 26)
            | ((in[14 + inpos] & 2047) << (17 - 11));
        out[27 + outpos] = ((in[14 + inpos] >>> 11) & 131071);
        out[28 + outpos] = (in[14 + inpos] >>> 28)
            | ((in[15 + inpos] & 8191) << (17 - 13));
        out[29 + outpos] = ((in[15 + inpos] >>> 13) & 131071);
        out[30 + outpos] = (in[15 + inpos] >>> 30)
            | ((in[16 + inpos] & 32767) << (17 - 15));
        out[31 + outpos] = (in[16 + inpos] >>> 15);
    }


}
