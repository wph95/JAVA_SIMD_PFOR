package io.dashbase.codec.io;

import org.apache.lucene.store.IndexOutput;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class MemorySegmentOutput extends IndexOutput {
    MemorySegment segment;
    int pos = 0;

    public MemorySegmentOutput(String resourceDescription, String name, MemorySegment segment) {
        super(resourceDescription, name);
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
    public long getChecksum() throws IOException {
        return 0;
    }

    @Override
    public void writeByte(byte b) {
        segment.set(ValueLayout.JAVA_BYTE, pos, b);
        pos++;
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) {
        MemorySegment.copy(b, 0, segment, ValueLayout.JAVA_BYTE, offset, length);
        pos += length;
    }

    @Override
    public void writeLong(long v) {
        segment.set(ValueLayout.JAVA_LONG, pos, v);
        pos += 8;
    }
}
