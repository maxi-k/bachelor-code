/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import org.checkerframework.checker.index.qual.Positive;

import java.nio.ByteBuffer;

/**
 * Encodes a single long integer using a {@link ByteBuffer},
 * as implemented by the superclass {@link SingleValueEncoding}.
 */
public class LongEncoding extends SingleValueEncoding<Long> {

    @Override
    protected @Positive int nativeByteCount() {
        return CodingContext.NATIVE.getLongSize();
    }

    @Override
    protected ByteBuffer intoByteBuffer(ByteBuffer buffer, Long object) {
        return buffer.putLong(object);
    }

    @Override
    protected Long fromByteBuffer(ByteBuffer buffer) {
        return buffer.getLong();
    }

    @Override
    public @Positive int numBytes() {
        return getContext().getLongSize();
    }
}
