package io.dashbase.codec;

import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

import static io.dashbase.codec.utils.ForUtil.BLOCK_SIZE;

public interface CodeC {

    public long encode(MemorySegment segment, int[] values) throws IOException;
    public long encode(MemorySegment segment, long[] values) throws IOException;

    public long[] decode(MemorySegment segment) throws IOException;
    public int[] decodeInt(MemorySegment segment) throws IOException;

}
