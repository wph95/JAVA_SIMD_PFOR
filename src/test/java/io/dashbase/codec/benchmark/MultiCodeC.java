package io.dashbase.codec.benchmark;

import io.dashbase.codec.simd.SIMDBinaryPacking;
import me.lemire.integercompression.*;
import me.lemire.integercompression.differential.Delta;
import me.lemire.integercompression.differential.IntegratedIntegerCODEC;
import me.lemire.integercompression.synth.ClusteredDataGenerator;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class MultiCodeC {


    public static int[][] createData() {
        ClusteredDataGenerator cdg = new ClusteredDataGenerator();
        int[][] data = generateTestData(cdg, 20, 18, 10);
        return data;
    }

    private static int[][] generateTestData(ClusteredDataGenerator dataGen,
                                            int N, int nbr, int sparsity) {
        final int[][] data = new int[N][];
        final int dataSize = (1 << (nbr + sparsity));
        for (int i = 0; i < N; ++i) {
            data[i] = dataGen.generateClustered((1 << nbr),
                                                dataSize);
        }
        return data;
    }

    public static void main(String args[]) throws FileNotFoundException {
        int[][] data = createData();
        testCodec(new Composition(new SIMDBinaryPacking(), new VariableByte()), data, 10000, true);
        testCodec(new Composition(new BinaryPacking(), new VariableByte()), data, 10000, true);
    }


    private static void testCodec(
        IntegerCODEC c, int[][] data, int repeat, boolean verbose) {
        if (verbose) {
            System.out.println("# " + c.toString());
            System.out
                .println("# bits per int, compress speed (mis), decompression speed (mis) ");
        }

        int N = data.length;

        int totalSize = 0;
        int maxLength = 0;
        for (int k = 0; k < N; ++k) {
            totalSize += data[k].length;
            if (data[k].length > maxLength) {
                maxLength = data[k].length;
            }
        }

        // 4x + 1024 to account for the possibility of some negative
        // compression.
        int[] compressBuffer = new int[4 * maxLength + 1024];
        int[] decompressBuffer = new int[maxLength + 1024];

        // These variables hold time in microseconds (10^-6).
        long compressTime = 0;
        long decompressTime = 0;

        int size = 0;
        IntWrapper inpos = new IntWrapper();
        IntWrapper outpos = new IntWrapper();

        for (int r = 0; r < repeat; ++r) {
            size = 0;
            for (int k = 0; k < N; ++k) {
                int[] backupdata = Arrays.copyOf(data[k],
                                                 data[k].length);

                // compress data.
                long beforeCompress = System.nanoTime() / 1000;
                inpos.set(1);
                outpos.set(0);
                if (!(c instanceof IntegratedIntegerCODEC)) {
                    Delta.delta(backupdata);
                }
                c.compress(backupdata, inpos, backupdata.length
                    - inpos.get(), compressBuffer, outpos);
                long afterCompress = System.nanoTime() / 1000;

                // measure time of compression.
                compressTime += afterCompress - beforeCompress;
                final int thiscompsize = outpos.get() + 1;
                size += thiscompsize;

                // extract (uncompress) data
                long beforeDecompress = System.nanoTime() / 1000;
                inpos.set(0);
                outpos.set(1);
                decompressBuffer[0] = backupdata[0];
                c.uncompress(compressBuffer, inpos,
                             thiscompsize - 1, decompressBuffer,
                             outpos);
                if (!(c instanceof IntegratedIntegerCODEC))
                    Delta.fastinverseDelta(decompressBuffer);
                long afterDecompress = System.nanoTime() / 1000;

                // measure time of extraction (uncompression).
                decompressTime += afterDecompress
                    - beforeDecompress;
                if (outpos.get() != data[k].length)
                    throw new RuntimeException(
                        "we have a bug (diff length) "
                            + c + " expected "
                            + data[k].length
                            + " got "
                            + outpos.get());

                // verify: compare original array with
                // compressed and
                // uncompressed.

                for (int m = 0; m < outpos.get(); ++m) {
                    if (decompressBuffer[m] != data[k][m]) {
                        throw new RuntimeException(
                            "we have a bug (actual difference), expected "
                                + data[k][m]
                                + " found "
                                + decompressBuffer[m]
                                + " at " + m + " out of " + outpos.get());
                    }
                }
            }
        }

        if (verbose) {
            double bitsPerInt = size * 32.0 / totalSize;
            long compressSpeed = (long) totalSize * repeat
                / (compressTime);
            long decompressSpeed = (long) totalSize * repeat
                / (decompressTime);
            System.out.println(String.format(
                "\t%1$.2f\t%2$d\t%3$d", bitsPerInt,
                compressSpeed, decompressSpeed));
        }
    }
}
