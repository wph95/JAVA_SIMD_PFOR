package io.dashbase.codec.utils;

import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;

import java.io.IOException;

public abstract class BaseForUtil {
    public static int BLOCK_SIZE = 128;
    private static int BLOCK_SIZE_LOG2 = 7;

    int numBytes(int bitsPerValue){
        return bitsPerValue << (BLOCK_SIZE_LOG2 - 3);
    };

    void decodeTo32(int bitsPerValue, DataInput in, long[] longs) throws IOException{
        throw new UnsupportedOperationException();
    };

    public void decode(int bitsPerValue, DataInput in, long[] longs) throws IOException{
        throw new UnsupportedOperationException();
    };

    public void encode(long[] longs, int bitsPerValue, DataOutput out) throws IOException{
        throw new UnsupportedOperationException();
    };

}
