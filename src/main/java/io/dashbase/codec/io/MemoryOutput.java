package io.dashbase.codec.io;

import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

import java.io.IOException;

public class MemoryOutput extends IndexOutput {



    public byte[] buffer;
    int pos = 0;
    /**
     * Sole constructor. resourceDescription should be non-null, opaque string describing this
     * resource; it's returned from {@link #toString}.
     *
     * @param resourceDescription
     * @param name
     */
    public MemoryOutput(String resourceDescription, String name, int size) {
        super(resourceDescription, name);
        buffer = new byte[size];
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
    public void writeByte(byte b) throws IOException {
        buffer[(int) getFilePointer()] = b;
        pos++;
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        System.arraycopy(b, offset, buffer, (int) getFilePointer(), length);
        pos += length;
    }


    public IndexInput toInput() {
        return new MemoryInput(getName(), buffer);
    }
}
