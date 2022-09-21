package io.dashbase.codec;

import org.apache.lucene.store.*;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.packed.PackedInts;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static io.dashbase.codec.ForUtil.*;

public class TestBinaryPack {
    final ForUtil forUtil = new ForUtil();

//    @Test
//    public void TestV() {
//        int[] myIntArray = new int[]{1, 2, 3};
//
//        var in = new long[]{4538991236898928L,
//            76879164074975601L,
//            149219336913052274L,
//            221559509751128947L,
//            293899682589205620L,
//            366239855427282293L,
//            438580028265358966L,
//            510920201103435639L,
//            583260373941512312L,
//            655600546779588985L,
//            727940719617665658L,
//            800280892455742331L,
//            872621065293819004L,
//            944961238131895677L,
//            1017301410969972350L,
//            1089641583808049023L};
//
//        simdExpand8(in);
//
//
//        System.out.println(Arrays.toString(in));
//
//
//    }
//
//    @Test
//    public void ED() throws IOException {
//        final Directory d = new ByteBuffersDirectory();
//        var out = d.createOutput("test.bin", IOContext.DEFAULT);
//        var source = new long[128];
//        long or = 0;
//
//        for (int i = 0; i < 128; i++) {
//            source[i] = i;
//            or |= source[i];
//        }
//        System.out.println(Arrays.toString(source));
//
//        //   7
//        final int bpv = PackedInts.bitsRequired(or);
//
//        forUtil.encode(source, bpv, out);
//        System.out.println(Arrays.toString(source));
//
//        out.close();
//
//
//        IndexInput in = d.openInput("test.bin", IOContext.READONCE);
//        long[] tmp = new long[BLOCK_SIZE / 2];
//        long[] restored = new long[BLOCK_SIZE];
//        int[] ints = new int[BLOCK_SIZE];
//
//        decode7(in, tmp, restored);
//        expand8(restored);
//
//        for (int j = 0; j < BLOCK_SIZE; ++j) {
//            ints[j] = Math.toIntExact(restored[j]);
//        }
//
//        System.out.println(Arrays.toString(ints));
//
//
//    }



}
