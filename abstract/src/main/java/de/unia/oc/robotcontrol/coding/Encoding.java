/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import de.unia.oc.robotcontrol.util.Bijection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Supplier;

/**
 * Interface for encoding Java objects and primitives into byte sequences
 * and vice versa. Intended for use in exchanging data between the
 * controlling device, sensors and actors.
 *
 * Classes extending this interface should be immutable. This prevents
 * confusion with changing one encoding, for example by setting a
 * different context, having an effect on another one, thus breaking
 * the communication with another device.
 *
 * @param <T> The type of Java Object a particular Instance of {@link Encoding}
 *            will encode and decode to.
 */
public interface Encoding<T> extends Bijection<T, byte[]>, Contextual {

    /**
     * {@inheritDoc}
     *
     * @param object The thing to encode
     * @return a byte representation of the given object
     * @throws IllegalArgumentException if the object could not be
     * encoded
     */
    @Override
    byte[] encode(T object) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     *
     * @param raw a byte representation of the value type coded by
     *            this instance of {@link Encoding}
     * @return a decoded value (from the given bytes)
     * @throws IllegalArgumentException if the object could not be
     * decoded from the given bytes
     */
    @Override
    @NonNull T decode(byte[] raw) throws IllegalArgumentException;

    /**
     * Return an encoding of type {@link T}  with its context
     * set to {@code context}
     * @param context The coding context that has to be set
     * @return An encoding with the context set to {@code context}
     */
    @Override
    default Encoding<T> withContext(CodingContext context) {
        Encoding<T> old = this;
        return new Encoding<T>() {

            @Override
            public CodingContext getContext() {
                return context;
            }

            @Override
            public byte[] encode(T object) throws IllegalArgumentException {
                return old.encode(object);
            }

            @Override
            public @NonNull T decode(byte[] raw) throws IllegalArgumentException {
                return old.decode(raw);
            }
        };
    }

    /**
     * {@inheritDoc}
     *
     * Transforms the type of value this encoding accepts and emits using
     * a {@link Bijection}
     *
     * @param top The instance of of {@link Bijection} that is used
     *            to encode first and decode last {@code (R <-> T)},
     *            transforming the encoding
     * @param <R> the new type this encoding can accept
     * @return A new instance of {@link Encoding}
     */
    @Override
    default <R> Encoding<R> stack(Bijection<R, T> top) {
        Encoding<T> bottom = this;
        return new Encoding<R>() {

            @Override
            public CodingContext getContext() { return bottom.getContext(); }

            @Override
            public byte[] encode(R object) throws IllegalArgumentException {
                return bottom.encode(top.encode(object));
            }

            @Override
            public @NonNull R decode(byte[] raw) throws IllegalArgumentException {
                return top.decode(bottom.decode(raw));
            }
        };
    }

    /**
     * A 'null' encoding that encodes an empty byte array
     * and decodes objects as provided by the given {@link Supplier}.
     *
     * @param context the coding context to return in {{@link #getContext()}}
     * @param supplier the supplier that is called in {@link #decode(byte[])}
     * @param <T> the value type of the encoding
     * @return new instance of {@link Encoding}
     */
    static <T> Encoding<T> nullEncoding(CodingContext context, Supplier<@NonNull T> supplier) {
        return new Encoding<T>() {

            @Override
            public CodingContext getContext() {
                return context;
            }

            @Override
            public byte[] encode(T object) throws IllegalArgumentException {
                return new byte[0];
            }

            @Override
            public @NonNull T decode(byte[] raw) throws IllegalArgumentException {
                return supplier.get();
            }
        };
    }
}
