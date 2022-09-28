package io.dashbase.local;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static jdk.incubator.vector.IntVector.*;

@State(Scope.Thread)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class Vector {


    @Param({ "vector256*2"})
    private String type;
    int[] in;
    IntVector[] inVec128 = new IntVector[32_000];
    IntVector[] inVec256 = new IntVector[32_000];
    IntVector[] inVec512 = new IntVector[32_000];
    int[] out = new int[32];
    IntVector[] outVec = new IntVector[32_000];

    @Setup
    public void setup() throws IOException {

        in = new int[32768];
        for (int i = 0; i < 32768; i++) {
            in[i] = i;
        }

        for (int i = 0; i < 2_048; i++) {
            inVec128[i] = IntVector.fromArray(SPECIES_128, in, i * 4);
            inVec256[i] = IntVector.fromArray(SPECIES_256, in, i * 8);
            inVec512[i] = IntVector.fromArray(SPECIES_512, in, i * 16);
        }

    }

    public void bit() {

        int inpos = 0;
        for (int i = 0; i < 16; i++) {
            out[i] = in[inpos] | ((in[1 + inpos]) << 1)
                | ((in[2 + inpos]) << 2) | ((in[3 + inpos]) << 3)
                | ((in[4 + inpos]) << 4) | ((in[5 + inpos]) << 5)
                | ((in[6 + inpos]) << 6) | ((in[7 + inpos]) << 7)
                | ((in[8 + inpos]) << 8) | ((in[9 + inpos]) << 9)
                | ((in[10 + inpos]) << 10) | ((in[11 + inpos]) << 11)
                | ((in[12 + inpos]) << 12) | ((in[13 + inpos]) << 13)
                | ((in[14 + inpos]) << 14) | ((in[15 + inpos]) << 15)
                | ((in[16 + inpos]) << 16) | ((in[17 + inpos]) << 17)
                | ((in[18 + inpos]) << 18) | ((in[19 + inpos]) << 19)
                | ((in[20 + inpos]) << 20) | ((in[21 + inpos]) << 21)
                | ((in[22 + inpos]) << 22) | ((in[23 + inpos]) << 23)
                | ((in[24 + inpos]) << 24) | ((in[25 + inpos]) << 25)
                | ((in[26 + inpos]) << 26) | ((in[27 + inpos]) << 27)
                | ((in[28 + inpos]) << 28) | ((in[29 + inpos]) << 29)
                | ((in[30 + inpos]) << 30) | ((in[31 + inpos]) << 31);
            inpos += 32;
        }

    }

    public void vector(IntVector[] inVec, int recycle) {
        int inpos = 0;

        for (int i = 0; i < recycle; i++) {
             outVec[i] = inVec[0]
                .or(inVec[1 + inpos].lanewise(VectorOperators.LSHL, 1))
                .or(inVec[2 + inpos].lanewise(VectorOperators.LSHL, 2))
                .or(inVec[3 + inpos].lanewise(VectorOperators.LSHL, 3))
                .or(inVec[4 + inpos].lanewise(VectorOperators.LSHL, 4))
                .or(inVec[5 + inpos].lanewise(VectorOperators.LSHL, 5))
                .or(inVec[6 + inpos].lanewise(VectorOperators.LSHL, 6))
                .or(inVec[7 + inpos].lanewise(VectorOperators.LSHL, 7))
                .or(inVec[8 + inpos].lanewise(VectorOperators.LSHL, 8))
                .or(inVec[9 + inpos].lanewise(VectorOperators.LSHL, 9))
                .or(inVec[10 + inpos].lanewise(VectorOperators.LSHL, 10))
                .or(inVec[11 + inpos].lanewise(VectorOperators.LSHL, 11))
                .or(inVec[12 + inpos].lanewise(VectorOperators.LSHL, 12))
                .or(inVec[13 + inpos].lanewise(VectorOperators.LSHL, 13))
                .or(inVec[14 + inpos].lanewise(VectorOperators.LSHL, 14))
                .or(inVec[15 + inpos].lanewise(VectorOperators.LSHL, 15))
                .or(inVec[16 + inpos].lanewise(VectorOperators.LSHL, 16))
                .or(inVec[17 + inpos].lanewise(VectorOperators.LSHL, 17))
                .or(inVec[18 + inpos].lanewise(VectorOperators.LSHL, 18))
                .or(inVec[19 + inpos].lanewise(VectorOperators.LSHL, 19))
                .or(inVec[20 + inpos].lanewise(VectorOperators.LSHL, 20))
                .or(inVec[21 + inpos].lanewise(VectorOperators.LSHL, 21))
                .or(inVec[22 + inpos].lanewise(VectorOperators.LSHL, 22))
                .or(inVec[23 + inpos].lanewise(VectorOperators.LSHL, 23))
                .or(inVec[24 + inpos].lanewise(VectorOperators.LSHL, 24))
                .or(inVec[25 + inpos].lanewise(VectorOperators.LSHL, 25))
                .or(inVec[26 + inpos].lanewise(VectorOperators.LSHL, 26))
                .or(inVec[27 + inpos].lanewise(VectorOperators.LSHL, 27))
                .or(inVec[28 + inpos].lanewise(VectorOperators.LSHL, 28))
                .or(inVec[29 + inpos].lanewise(VectorOperators.LSHL, 29))
                .or(inVec[30 + inpos].lanewise(VectorOperators.LSHL, 30))
                .or(inVec[31 + inpos].lanewise(VectorOperators.LSHL, 31));
            inpos += 32;


        }

    }

    @Benchmark
    public void encode() throws IOException {
        switch (type) {
//            case "bit*16" -> bit();
            case "vector128*4" -> vector(inVec128, 64);
            case "vector256*2" -> vector(inVec256, 64);
            case "vector512" -> vector(inVec512, 64);
            default -> throw new RuntimeException("Unknown type: " + type);
        }
    }

//    @BaseBenchmark
//    public void prepare() {
//        switch (type) {
//            case "bit*16" -> {
//            }
//            case "vector128*4" -> createVec(SPECIES_128, 1);
//            case "vector256*2" -> createVec(SPECIES_256, 1);
//            case "vector512" -> createVec(SPECIES_512, 1);
//            default -> throw new RuntimeException("Unknown type: " + type);
//        }
//    }
//
//    void createVec(VectorSpecies<Integer> species, int recycle) {
//        for (int j = 0; j < recycle; j++) {
//            IntVector[] tt = new IntVector[32];
//            for (int i = 0; i < 32; i++) {
//                tt[i] = IntVector.fromArray(species, in, i * 32);
//            }
//
//        }
//
//
//    }


}
