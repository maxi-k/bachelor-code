/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.ByteBuffer;

/**
 * Encodes a single single-precision floating point number
 * using a {@link ByteBuffer},
 * as implemented by the superclass {@link SingleValueEncoding}.
 */
public class FloatEncoding extends SingleValueEncoding<Float> {
    @Override
    protected @Positive int nativeByteCount() {
        return CodingContext.NATIVE.getFloatSize();
    }

    @Override
    protected ByteBuffer intoByteBuffer(ByteBuffer buffer, @NonNull Float object) {
        return buffer.putFloat(object);
    }

    @Override
    protected Float fromByteBuffer(ByteBuffer buffer) {
        return buffer.getFloat();
    }

    @Override
    public @Positive int numBytes() {
        return getContext().getFloatSize();
    }
}
