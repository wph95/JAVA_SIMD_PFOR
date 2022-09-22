package io.dashbase.codec;

import io.dashbase.codec.benchmark.BaseBenchmark;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestPerf {
    @Test
    public void testSIMD() throws IOException {
        var bench = new BaseBenchmark();
        bench.init(12);
        for (int i = 0; i < 10_000_000; i++) {
            bench.luceneDecode();
        }
        for (int i = 0; i < 10_000_000; i++) {
            bench.simdDecode(BaseBenchmark.SIMDType.SIMD512, 1);
        }
    }
}
