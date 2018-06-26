/* 2016 */
package de.unia.oc.robotcontrol.util;

import java.util.function.Function;

/**
 * A generic functional interface for bijective transformations from
 * type {@link T} to type {@link F} and vice versa (short: T <-> F)
 *
 * The transformation from type {@link T} into type {@link F} (T -> F)
 * is considered encoding ({@link #encode}), whereas the reverse transformation (F -> T)
 * is considered decoding ({@link #decode}).
 *
 * @param <T> The 'unencoded' type.
 * @param <F> The 'encoded' type.
 */
public interface Bijection<T, F> {

    /**
     * Transforms the given object of type {@link T} into
     * something of the encoded type {@link F}.
     * @param object The thing to transform / encode
     * @return The transformed / encoded object
     * @throws IllegalArgumentException if the object is not transformable
     * or a valid target for encoding (for example, {@code null}).
     */
    F encode(T object) throws IllegalArgumentException;

    /**
     * Transforms the given object of type {@link F} into
     * something of the decoded type {@link T}.
     * @param raw The thing to transform / decode
     * @return The transformed / decoded object
     * @throws IllegalArgumentException if the object is not transformable
     * or a valid target for decoding (for example, {@code null}).
     */
    T decode(F raw) throws IllegalArgumentException;

    /**
     * Utility function that returns the {@link #encode} method as an
     * instance of the {@link java.util.function.Function} interface
     * @return The encoding function
     */
    default Function<T, F> encoder() { return this::encode; }

    /**
     * Utility function that returns the {@link #decode} method as an
     * instance of the {@link java.util.function.Function} interface
     * @return The decoding function
     */
    default Function<F, T> decoder() { return this::decode; }

    /**
     * Builds a new {@link Bijection} interface that uses
     * this instance as well as the given instance to decode and encode:
     * (T <-> F), (R <-> T) => (R <-> F)
     * @param top The instance of of {@link Bijection} that is used
     *            to encode first and decode last (R <-> T)
     * @param <R> The type that is used for encoding and targeted by decoding
     * @return A new {@link Bijection} interface (R <-> F)
     */
    default <R> Bijection<R, F> stack(
            Bijection<R, T> top) {
        return Bijection.create(
                top.encoder().andThen(this.encoder()),
                this.decoder().andThen(top.decoder())
        );
    }

    /**
     * Builds a new {@link Bijection} interface that uses
     * this instance as well as the given instance to decode and encode:
     * (T <-> F), (F <-> R) => (T <-> R)
     * @param bottom The instance of of {@link Bijection} that is used
     *               to encode last and decode first (F <-> R)
     * @param <R> The type that is targeted by encoding and used for decoding
     * @return A new {@link Bijection} interface (T <-> R)
     */
    default <R> Bijection<T, R> supplement(
            Bijection<F, R> bottom
    ) {
        return bottom.stack(this);
    }

    /**
     * Builds a new {@link Bijection} interface that uses
     * this instance as well as the given instances to decode and encode:
     * (T <-> F), (R <-> T), (F <-> S) => (T <-> R)
     *
     * @param top The instance of of {@link Bijection} that is used
     *            to encode first and decode last (R <-> T)
     * @param bottom The instance of of {@link Bijection} that is used
     *               to encode last and decode first (F <-> S)
     * @param <R> The type that is used for encoding and targeted by decoding
     * @param <S> The type that is targeted by encoding and used for decoding
     * @return A new {@link Bijection} interface (R <-> S)
     */
    default <R, S> Bijection<R, S> wrap(
            Bijection<R, T> top,
            Bijection<F, S> bottom
    ) {
        return bottom.stack(this).stack(top);
    }

    /**
     * Creates a {@link Bijection} instance (A <-> B)
     * given an encoder function (A -> B) and a decoder function (B -> A).
     *
     * @param encoder The function used for encoding (A -> B)
     * @param decoder The function used for decoding (B -> A)
     * @param <A> The type of the unencoded value
     * @param <B> The type of the encoded value
     * @return A new instance of {@link Bijection} (A <-> B)
     */
    static <A, B> Bijection<A, B> create(
            Function<A, B> encoder,
            Function<B, A> decoder) {
        return new Bijection<A, B>() {
            @Override
            public B encode(A object) throws IllegalArgumentException {
                return encoder.apply(object);
            }

            @Override
            public A decode(B raw) throws IllegalArgumentException {
                return decoder.apply(raw);
            }
        };
    }

}
