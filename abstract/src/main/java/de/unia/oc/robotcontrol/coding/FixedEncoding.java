/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import de.unia.oc.robotcontrol.util.Bijection;
import de.unia.oc.robotcontrol.util.Tuple;

public interface FixedEncoding<T> extends Encoding<T> {

    /**
     *
     * @return The number of bytes this Encoding needs.
     */
    int numBytes();

    default <R> FixedEncoding<R> fromEncoding(Encoding<R> encoding, int numBytes) {
        return new FixedEncoding<R>() {
            @Override
            public int numBytes() {
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
            public R decode(byte[] raw) throws IllegalArgumentException {
                return encoding.decode(raw);
            }
        };
    }

    @Override
    default FixedEncoding<T> withContext(CodingContext context) {
        FixedEncoding<T> self = this;
        return new FixedEncoding<T>() {
            @Override
            public int numBytes() {
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
            public T decode(byte[] raw) throws IllegalArgumentException {
                return self.decode(raw);
            }
        };
    }

    default <S, R> FixedEncoding<R> append(FixedEncoding<S> second, Bijection<Tuple<T, S>, R> joiner) {
        FixedEncoding<T> first = this;

        if (first.getContext() != second.getContext()) {
            throw new IllegalArgumentException("Encoding Contexts must match!");
        }

        return new FixedEncoding<R>() {

            @Override
            public int numBytes() {
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
                        .decode(object)
                        .map(first::encode, second::encode)
                        .joinWith(CodingUtil::join);
            }

            @Override
            public R decode(byte[] raw) throws IllegalArgumentException {
                return CodingUtil
                        .splitAt(raw, first.numBytes())
                        .map(first::decode, second::decode)
                        .joinWith(joiner::encode);
            }
        };
    }
}
