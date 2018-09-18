/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import java.nio.ByteBuffer;

/**
 * Encodes a single integer using a {@link ByteBuffer},
 * as implemented by the superclass {@link SingleValueEncoding}.
 */
public class IntegerEncoding extends SingleValueEncoding<Integer> {

    public IntegerEncoding() {
        super();
    }

    public IntegerEncoding(CodingContext context) {
        super(context);
    }

    @Override
    protected int nativeByteCount() {
        return CodingContext.NATIVE.getIntSize();
    }

    @Override
    public int numBytes() {
        return getContext().getIntSize();
    }

    @Override
    protected ByteBuffer intoByteBuffer(ByteBuffer buffer, Integer object) {
        return buffer.putInt(object);
    }

    @Override
    protected Integer fromByteBuffer(ByteBuffer buffer) {
        return buffer.getInt();
    }

}
