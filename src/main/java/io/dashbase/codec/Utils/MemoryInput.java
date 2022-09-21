package io.dashbase.codec.Utils;

import org.apache.lucene.store.IndexInput;

import java.io.IOException;

public class MemoryInput extends IndexInput {
    /**
     * resourceDescription should be a non-null, opaque string describing this resource; it's returned
     * from {@link #toString}.
     *
     * @param resourceDescription
     */
    byte[] buffer;
    int pos = 0;
    int end;
    public MemoryInput(String resourceDescription, byte[] buffer) {
        super(resourceDescription);
        this.buffer = buffer;
        end = buffer.length;
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
        var slice = new byte[(int) length];
        System.arraycopy(buffer, (int) offset, slice, 0, (int) length);
        return new MemoryInput(sliceDescription, slice);
    }

    @Override
    public byte readByte() throws IOException {
        var v = buffer[(int) getFilePointer()];
        pos++;
        return v;
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
        System.arraycopy(buffer, (int) getFilePointer(), b, offset, len);
        pos += len;
    }
}
