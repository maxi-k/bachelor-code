/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * An abstract class implementing {@link Encoding},
 * that represents a single value being encoded as bytes.
 * @param <T> The type of the objects to be encoded
 */
public abstract class SingleValueEncoding<T> implements FixedEncoding<T> {

    /**
     * The CodingContext used for this Encoding.
     */
    private final CodingContext context;

    public SingleValueEncoding(CodingContext context) {
        this.context = context;
    }

    public SingleValueEncoding() {
        this(CodingContext.NATIVE);
    }


    /**
     *
     * @return The amount of bytes an object of type {@link T} would
     *         use if encoded for the machine this is running on
     *         ({@link CodingContext#NATIVE}).
     */
    protected abstract @Positive int nativeByteCount();

    /**
     * Puts the given object into a {@link ByteBuffer},
     * returning a {@link ByteBuffer} with the object added.
     *
     * @param buffer The {@link ByteBuffer} to put the given object into.
     * @param object The object to put into the given {@link ByteBuffer}
     * @return A byte buffer with the contents of the given one, with the
     *         given object added to it.
     */
    protected abstract ByteBuffer intoByteBuffer(ByteBuffer buffer, @NonNull T object);

    /**
     * Extracts an the object to be decoded out of the given byte buffer.
     * @param buffer The byte buffer to extract the data for the object
     *               to be returned into
     * @return An instance of an object of type {@link T}, extracted out of
     *         the byte buffer.
     */
    protected abstract @NonNull T fromByteBuffer(ByteBuffer buffer);

    @Override
    public CodingContext getContext() {
        return context;
    }

    @Override
    public @NonNull T decode(byte[] raw) throws IllegalArgumentException {
        if (raw == null) { throw new IllegalArgumentException("Passed bytes were null!"); }
        if (getContext().doReverse()) { CodingUtil.reverseBytes(raw); }
        byte[] nativeSize = Arrays.copyOf(raw, this.nativeByteCount());
        return fromByteBuffer(
                ByteBuffer.wrap(nativeSize)
                          .order(CodingContext.NATIVE.getByteOrder())
        );
    }

    @Override
    public @NonNull byte[] encode(T object) {
        if (object == null) { throw new IllegalArgumentException("Passed object was null!"); }
        byte[] whole = intoByteBuffer(
                ByteBuffer
                        .allocate(nativeByteCount())
                        .order(getContext().getByteOrder()),
                object
        ).array();
        return getContext().getByteOrder() == ByteOrder.BIG_ENDIAN ?
                Arrays.copyOfRange(
                        whole,
                        (whole.length - numBytes()),
                        whole.length) :
                Arrays.copyOfRange(
                        whole,
                        0,
                        numBytes());
    }
}
