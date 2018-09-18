/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.ByteBuffer;

/**
 * Encodes a single double-precision floating point number
 * using a {@link ByteBuffer},
 * as implemented by the superclass {@link SingleValueEncoding}.
 */
public class DoubleEncoding extends SingleValueEncoding<Double> {
    @Override
    protected @Positive int nativeByteCount() {
        return CodingContext.NATIVE.getDoubleSize();
    }

    @Override
    protected ByteBuffer intoByteBuffer(ByteBuffer buffer, @NonNull Double object) {
        return buffer.putDouble(object);
    }

    @Override
    protected Double fromByteBuffer(ByteBuffer buffer) {
        return buffer.getDouble();
    }

    @Override
    public @Positive int numBytes() {
        return getContext().getDoubleSize();
    }
}
