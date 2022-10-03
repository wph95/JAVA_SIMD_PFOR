package io.dashbase.codec.utils;

import com.carrotsearch.randomizedtesting.generators.RandomNumbers;

import java.util.Random;

public class TestUtil {
    public static int nextInt(Random r, int start, int end) {
        return RandomNumbers.randomIntBetween(r, start, end);
    }
}
