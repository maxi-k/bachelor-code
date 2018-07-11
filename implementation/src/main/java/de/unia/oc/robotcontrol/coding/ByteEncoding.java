package de.unia.oc.robotcontrol.coding;

import java.nio.ByteBuffer;

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
