package io.dashbase.codec.simd;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.SkippableIntegerCODEC;
import me.lemire.integercompression.Util;

import java.util.Arrays;

public class SIMDBinaryPacking extends BinaryPack implements IntegerCODEC, SkippableIntegerCODEC {

    final int BLOCK_SIZE;

    public SIMDBinaryPacking() {
        super(IntVector.SPECIES_512);
        BLOCK_SIZE = IntVector.SPECIES_512.length() * 32;
    }

    public SIMDBinaryPacking(VectorSpecies<Integer> species) {
        super(species);
        BLOCK_SIZE = species.length() * 32;
    }

    @Override
    public void compress(int[] in, IntWrapper inpos, int inlength, int[] out, IntWrapper outpos) {
        inlength = Util.greatestMultiple(inlength, SPECIES.length());
        if (inlength == 0)
            return;
        out[outpos.get()] = inlength;
        outpos.increment();
        headlessCompress(in, inpos, inlength, out, outpos);

    }

    @Override
    public void uncompress(int[] in, IntWrapper inpos, int inlength, int[] out, IntWrapper outpos) {
        if (inlength == 0)
            return;
        final int outlength = in[inpos.get()];
        inpos.increment();
        headlessUncompress(in, inpos, inlength, out, outpos, outlength);

    }

    @Override
    public void headlessCompress(int[] in, IntWrapper inpos, int inlength, int[] out, IntWrapper outpos) {
        inlength = Util.greatestMultiple(inlength, BLOCK_SIZE);
        int tmpoutpos = outpos.get();
        int s = inpos.get();

        for (int i = 0; i < (inlength / BLOCK_SIZE); i++) {
            int[] tmpin = new int[BLOCK_SIZE];
            System.arraycopy(in, s, tmpin, 0, BLOCK_SIZE);
            s += BLOCK_SIZE;

            var inVec = new IntVector[32];
            var bitSize = createVec(tmpin, inVec);

            out[tmpoutpos] = bitSize;
            tmpoutpos++;

            var o = encode(inVec, bitSize);

            System.arraycopy(o, 0, out, tmpoutpos, o.length);
            tmpoutpos += o.length;


        }
        inpos.add(inlength);
        outpos.add(tmpoutpos);

    }

    @Override
    public void headlessUncompress(int[] in, IntWrapper inpos, int inlength, int[] out, IntWrapper outpos, int num) {
        final int outlength = Util.greatestMultiple(num, BLOCK_SIZE);
        int tmpinpos = inpos.get();
        int s = outpos.get();
        for (int i = 0; i < (inlength / BLOCK_SIZE); i++) {
            int bitSize = in[tmpinpos];
            tmpinpos++;
            var blockSize = bitSize * VECTOR_LENGTH;
            int[] tmpin = new int[blockSize];
            System.arraycopy(in, tmpinpos, tmpin, 0, blockSize);
            tmpinpos += blockSize;

            var inVec = createVec(tmpin);
            var results = decode(inVec, bitSize);
            System.arraycopy(results, 0, out, s, results.length);
            outpos.add(results.length);
        }
        inpos.set(tmpinpos);
    }

}
