package io.dashbase.codec.utils;

import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;

import java.io.IOException;

public interface BasePForUtil {
    public int BLOCK_SIZE = 128;
    public void encode(long[] longs, DataOutput out) throws IOException;

    public void decode(DataInput in, long[] longs) throws IOException;

    public void decodeAndPrefixSum(DataInput in, long base, long[] longs) throws IOException;

    public void skip(DataInput in) throws IOException;
}
