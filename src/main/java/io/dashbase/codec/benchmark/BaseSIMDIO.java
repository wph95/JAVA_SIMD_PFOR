package io.dashbase.codec.benchmark;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BaseSIMDIO {

    public static int[] createRandomArray(int size) {
        int[] out = new int[size];
        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < size; i++) {
            out[i] = random.nextInt();
        }
        return out;
    }

    public static int[] bytebuf2array(ByteBuffer buf, int size) {
        int[] out = new int[size];
        for (int i = 0; i < size; i++) {
            final byte b1 = buf.get(i * 4);
            final byte b2 = buf.get(i * 4 + 1);
            final byte b3 = buf.get(i * 4 + 2);
            final byte b4 = buf.get(i * 4 + 3);
            out[i] = ((b4 & 0xFF) << 24) | ((b3 & 0xFF) << 16) | ((b2 & 0xFF) << 8) | (b1 & 0xFF);
        }
        return out;
    }

    public static int[] intbuf2array(ByteBuffer buf, int size) {
        int[] out = new int[size];
        for (int i = 0; i < size; i++) {
            out[i] = buf.getInt(i * 4);
        }
        return out;
    }


    public static IntVector[] memorySegment2vector(VectorSpecies<Integer> species, MemorySegment segment, int size) {
        int vectorSize = species.length();
        int vectorCount = size / vectorSize;
        IntVector[] vectors = new IntVector[vectorCount];
        for (int i = 0; i < vectorCount; i++) {
            vectors[i] = IntVector.fromMemorySegment(species, segment, (long) i * vectorSize * 4, ByteOrder.nativeOrder());
        }
        return vectors;
    }


    public static int[] memorySegment2intArr( MemorySegment segment, int size) {
        int[] out = new int[size];
        for (int i = 0; i < size; i++) {
            out[i] = segment.get(ValueLayout.JAVA_INT, (long) i * 4);
        }
        return out;

    }
}
