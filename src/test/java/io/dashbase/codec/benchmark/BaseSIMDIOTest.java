package io.dashbase.codec.benchmark;

import jdk.incubator.vector.IntVector;
import org.junit.jupiter.api.Test;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class BaseSIMDIOTest {

    final static MemorySession session = MemorySession.openConfined();

    public int[] createRandomArray(int size) {
        int[] out = new int[size];
        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < size; i++) {
            out[i] = random.nextInt();
        }
        return out;
    }


    @Test
    void bytebuf2array() {
        int size = 16_000;
        ByteBuffer buf = ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder());
        var in = createRandomArray(size);

        for (int i = 0; i < size; i++) {
            buf.putInt(i * 4, in[i]);
        }
        int[] out = BaseSIMDIO.bytebuf2array(buf, size);
        assertArrayEquals(in, out);
    }

    @Test
    void intbuf2array() {
        int size = 16_000;
        ByteBuffer buf = ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder());
        var in = createRandomArray(size);
        for (int i = 0; i < size; i++) {
            buf.putInt(i * 4, in[i]);
        }
        int[] out = BaseSIMDIO.intbuf2array(buf, size);
        assertArrayEquals(in, out);
    }

    @Test
    void memorySegment2vector() {
        int size = 16_000;

        MemorySegment memorySegment = MemorySegment.allocateNative(size * 4, session);
        var in = createRandomArray(size);
        for (int i = 0; i < size; i++) {
            memorySegment.set(ValueLayout.JAVA_INT, (long) i * 4, in[i]);
        }

        var out = BaseSIMDIO.memorySegment2vector(IntVector.SPECIES_512, memorySegment, size);
    }
}