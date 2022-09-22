package io.dashbase.codec;

import io.dashbase.codec.Utils.MemoryInput;
import io.dashbase.codec.Utils.MemoryOutput;
import io.dashbase.codec.simd.BinaryPack;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

import java.io.IOException;

import static io.dashbase.codec.BaseBenchmark.SIMDType.*;
import static jdk.incubator.vector.IntVector.*;

public class BaseBenchmark {
    public enum SIMDType {
        SIMD128,
        SIMD256,
        SIMD512,
    }

    public byte[] luceneCache;
    public IntVector[][] simd128Cache;
    public IntVector[][] simd256Cache;
    public IntVector[][] simd512Cache;

    public long[] dataL = new long[512];
    public int[] dataI = new int[512];

    public Lucene lucene = new Lucene();
    public BinaryPack codec128 = new BinaryPack(SPECIES_128);
    public BinaryPack codec256 = new BinaryPack(SPECIES_256);
    public BinaryPack codec512 = new BinaryPack(SPECIES_512);


    public void init(int bitSize) throws IOException {

        var MAX = 1 << (bitSize + 1) - 1;
        dataL = new long[512];
        dataI = new int[512];
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 8; j++) {
                dataI[i * 8 + j] += Math.abs(MAX - 256 + i * 8 + j) % MAX;
                dataL[i * 8 + j] += Math.abs(MAX - 256 + i * 8 + j) % MAX;
            }
        }

        var output = new MemoryOutput("test", "test.bin", 512 * 4);
        lucene.encode(output, dataL);
        luceneCache = output.buffer;

        simd128Cache = simdEncode(SIMD128, 4);
        simd256Cache = simdEncode(SIMD256, 2);
        simd512Cache = simdEncode(SIMD512, 1);
    }

    public BinaryPack getCodec(SIMDType type) {
        return switch (type) {
            case SIMD128 -> codec128;
            case SIMD256 -> codec256;
            case SIMD512 -> codec512;
        };
    }

    public IntVector[][] getCache(SIMDType type) {
        return switch (type) {
            case SIMD128 -> simd128Cache;
            case SIMD256 -> simd256Cache;
            case SIMD512 -> simd512Cache;
        };
    }


    public IntVector[][] simdEncode(SIMDType type, int size) throws IOException {
        BinaryPack codec = getCodec(type);
        var compressed = new IntVector[size][32];
        for (int i = 0; i < size; i++) {
            compressed[i] = codec.encodeV(dataI);
        }
        return compressed;
    }

    public void simdDecode(SIMDType type, int size) throws IOException {
        BinaryPack codec = getCodec(type);
        var cache = getCache(type);

        for (int i = 0; i < size; i++) {
            codec.decode(cache[i], 7);
        }
    }

    public void luceneEncode() throws IOException {
        for (int i = 0; i < 4; i++) {
            var output = new MemoryOutput("test", "test.bin", 512 * 4);
            lucene.encode(output, dataL);
        }
    }

    public void luceneDecode() throws IOException {
        for (int i = 0; i < 4; i++) {
            var input = new MemoryInput("test", luceneCache);
            lucene.decode(input);
        }
    }


}
