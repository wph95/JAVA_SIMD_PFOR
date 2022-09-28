package io.dashbase.codec.io;

import org.apache.lucene.store.IndexInput;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class MemorySegmentInput extends IndexInput {
    /**
     * resourceDescription should be a non-null, opaque string describing this resource; it's returned
     * from {@link #toString}.
     *
     * @param resourceDescription
     */
    MemorySegment segment;
    int pos = 0;
    int end;

    public MemorySegmentInput(String resourceDescription, MemorySegment segment) {
        super(resourceDescription);
        this.segment = segment;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public long getFilePointer() {
        return pos;
    }

    @Override
    public void seek(long pos) throws IOException {
        this.pos = (int) pos;
    }

    @Override
    public long length() {
        return end;
    }

    @Override
    public IndexInput slice(String sliceDescription, long offset, long length) throws IOException {
        var slice = segment.asSlice(offset, length);
        return new MemorySegmentInput(sliceDescription, slice);
    }

    @Override
    public byte readByte() throws IOException {
        var v = segment.get(ValueLayout.JAVA_BYTE, pos);
        pos++;
        return v;
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
        MemorySegment.copy(segment, ValueLayout.JAVA_BYTE, pos + offset, b, 0, len);
        pos += len;
    }

    @Override
    public long readLong() {
        var v = segment.get(ValueLayout.JAVA_LONG, pos);
        pos += 8;
        return v;
    }

    @Override
    public void readLongs(long[] dst, int offset, int length) throws IOException {
        MemorySegment.copy(segment, ValueLayout.JAVA_LONG, pos, dst, 0, length);
        pos += length * 8;
    }
}
