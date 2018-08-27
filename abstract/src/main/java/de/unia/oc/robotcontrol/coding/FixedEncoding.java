/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import de.unia.oc.robotcontrol.util.Bijection;
import de.unia.oc.robotcontrol.util.Tuple;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.NullnessUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface FixedEncoding<T> extends Encoding<T> {

    /**
     *
     * @return The number of bytes this Encoding needs.
     */
    @Positive int numBytes();

    /**
     * Return an instance of {@link FixedEncoding}, asserting that encoding
     * an instance of {@link R} uses the given number of bytes exactly.
     *
     * @param encoding the {@link Encoding} instance to wrap
     * @param numBytes the number of byte the Encoding is asserted to use
     * @param <R> the type of Object the given Encoding can encode
     * @return a new instance of {@link FixedEncoding} that uses the given
     *         encoding internally
     */
    default <R> FixedEncoding<R> fromEncoding(Encoding<R> encoding, int numBytes) {
        return new FixedEncoding<R>() {
            @Override
            public @Positive int numBytes() {
                return numBytes;
            }

            @Override
            public CodingContext getContext() {
                return encoding.getContext();
            }

            @Override
            public byte[] encode(R object) throws IllegalArgumentException {
                return encoding.encode(object);
            }

            @Override
            public @NonNull R decode(byte[] raw) throws IllegalArgumentException {
                return encoding.decode(raw);
            }
        };
    }

    @Override
    default FixedEncoding<T> withContext(CodingContext context) {
        FixedEncoding<T> self = this;
        return new FixedEncoding<T>() {
            @Override
            public @Positive int numBytes() {
                return self.numBytes();
            }

            @Override
            public CodingContext getContext() {
                return context;
            }

            @Override
            public byte[] encode(T object) throws IllegalArgumentException {
                return self.encode(object);
            }

            @Override
            public @NonNull T decode(byte[] raw) throws IllegalArgumentException {
                return self.decode(raw);
            }
        };
    }

    /**
     * Creates an new FixedEncoding instance which appends the bytes used
     * for the `second` parameter to those of the first parameter.
     * The encodable value is thus a tuple, which is then transformed using the
     * given `joiner` bijection (to- and from a tuple).
     *
     * @param second The encoding which to append to the first
     * @param joiner The bijection which can produce a value based on the tuple produced by the first and second encoding and vice versa
     * @param <S> The type of the second encoding
     * @param <R> The type of the joined value
     * @return An instance of FixedEncoding which can encode R
     */
    default <S, R> FixedEncoding<R> append(FixedEncoding<S> second, Bijection<R, Tuple<T, S>> joiner) {
        FixedEncoding<T> first = this;

        if (first.getContext() != second.getContext()) {
            throw new IllegalArgumentException("Encoding Contexts must match!");
        }

        return new FixedEncoding<R>() {

            @Override
            public @Positive int numBytes() {
                return first.numBytes() + second.numBytes();
            }

            @Override
            public CodingContext getContext() {
                return first.getContext();
            }

            @Override
            public FixedEncoding<R> withContext(CodingContext context) {
                return first.withContext(context).append(second.withContext(context), joiner);
            }

            @Override
            public byte[] encode(R object) throws IllegalArgumentException {
                return joiner
                        .encode(object)
                        .map(first::encode, second::encode)
                        .joinWith(CodingUtil::join);
            }

            @Override
            // TODO: Find out why checker-framework requires casting here
            public @NonNull R decode(byte[] raw) throws IllegalArgumentException {
                return NullnessUtil.castNonNull(
                        CodingUtil
                                .splitAt(raw, first.numBytes())
                                .map(first::decode, second::decode)
                                .joinWith(joiner::decode)
                );
            }
        };
    }

    /**
     * Creates a new instance of FixedEncoding that appends the bytes of the given
     * second encoding to the bytes of this one, and decodes to a tuple of type
     * (T, S).
     * @param second the encoding which to append to this one
     * @param <S> the type of the second encoding
     * @return An instance of FixedEncoding that can encode a Tuple (T, S)
     */
    default <S> FixedEncoding<Tuple<T, S>> append(FixedEncoding<S> second) {
        return append(second, Bijection.identity());
    }
}
