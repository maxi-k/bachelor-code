/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import de.unia.oc.robotcontrol.util.Bijection;

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

    @Override
    byte[] encode(T object) throws IllegalArgumentException;

    @Override
    T decode(byte[] raw) throws IllegalArgumentException;

    /**
     * Return an encoding of type {@link T}  with its context
     * set to {@param context}
     * @param context The conding context that has to be set
     * @return An encoding with the context set to {@param context}
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
            public T decode(byte[] raw) throws IllegalArgumentException {
                return old.decode(raw);
            }
        };
    }

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
            public R decode(byte[] raw) throws IllegalArgumentException {
                return top.decode(bottom.decode(raw));
            }
        };
    }
}
