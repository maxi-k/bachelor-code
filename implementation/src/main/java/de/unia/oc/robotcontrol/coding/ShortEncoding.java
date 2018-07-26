/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.ByteBuffer;

public class ShortEncoding extends SingleValueEncoding<Short> {

    public ShortEncoding() { super(); }

    @Override
    protected @Positive int nativeByteCount() {
        return CodingContext.NATIVE.getShortSize();
    }

    @Override
    protected ByteBuffer intoByteBuffer(ByteBuffer buffer, @NonNull Short object) {
        return buffer.putShort(object);
    }

    @Override
    protected Short fromByteBuffer(ByteBuffer buffer) {
        return buffer.getShort();
    }

    @Override
    public @Positive int numBytes() {
        return getContext().getShortSize();
    }
}
