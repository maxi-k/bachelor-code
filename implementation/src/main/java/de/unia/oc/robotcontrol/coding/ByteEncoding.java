package de.unia.oc.robotcontrol.coding;

import java.nio.ByteBuffer;

/**
 * Encodes a single byte using a {@link ByteBuffer},
 * as implemented by the superclass {@link SingleValueEncoding}.
 */
public class ByteEncoding extends SingleValueEncoding<Byte> {

    public ByteEncoding(CodingContext context) {
        super(context);
    }

    @Override
    protected int nativeByteCount() {
        return 1;
    }

    @Override
    protected ByteBuffer intoByteBuffer(ByteBuffer buffer, Byte object) {
        return buffer.put(object);
    }

    @Override
    protected Byte fromByteBuffer(ByteBuffer buffer) {
        return buffer.get();
    }

    @Override
    public int numBytes() {
        return 1;
    }
}
