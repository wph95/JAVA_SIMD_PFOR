package io.dashbase.codec.utils;

import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;

import java.io.IOException;

public abstract class BasePForUtil {
    public final int BLOCK_SIZE;

    protected BasePForUtil(int block_size) {
        BLOCK_SIZE = block_size;
    }

    public void encode(long[] longs, DataOutput out) throws IOException{
        throw new RuntimeException("not implemented");
    }

    public void decode(DataInput in, long[] longs) throws IOException {

    }

    public void decodeAndPrefixSum(DataInput in, long base, long[] longs) throws IOException {

    }

    public void skip(DataInput in) throws IOException {

    }

}
