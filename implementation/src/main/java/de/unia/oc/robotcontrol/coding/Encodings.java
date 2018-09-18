package de.unia.oc.robotcontrol.coding;

import de.unia.oc.robotcontrol.util.Bijection;
import de.unia.oc.robotcontrol.util.Tuple;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Supplier;

/**
 * Class with utility methods for the coding module of
 * the project ({@link de.unia.oc.robotcontrol.coding})
 * ("module-class").
 *
 * This only contains static methods and cannot be instantiated.
 */
public final class Encodings {

    private Encodings() {}

    /**
     * Stack a {@link Bijection} on top of the given encoding
     * to create a new encoding, which will pass its values it should
     * encode through the {@link Bijection} first, and pass values
     * it decoded through the {@link Bijection} afterwards.
     *
     * Mirror of {@link Encoding#stack(Bijection)}.
     *
     * @param bottom the encoding to modify
     * @param top the bijection to use to transform the decoded values
     * @param <F> the type of the values encoded by the original encoding
     * @param <R> the type of the values encoded by the new encoding
     * @return a new instance of {@link Encoding}
     */
    public static <F, R> Encoding<R> stack(
            Encoding<F> bottom,
            Bijection<R, F> top) {
        return bottom.stack(top);
    }

    /**
     * Join the two given {@link FixedEncoding} instances together
     * by appending them to each other, creating a {@link FixedEncoding}
     * for the tuple of the encoded values, which is passed to the given
     * {@link Bijection} to combine them.
     *
     * Mirror of {@link FixedEncoding#append(FixedEncoding)}.
     *
     * @param first the encoding associated with the first value of the tuple
     * @param second the encoding associated with the second value of the tuple
     * @param joiner the function which joins both together
     * @param <F> the type of values encoded by the first encoding
     * @param <S> the type of values encoded by the second encoding
     * @param <R> the type of values produced and consumed by the bijection,
     *           and consequently the resulting encoding
     *
     * @return a new instance of {@link FixedEncoding}
     */
    public static <F, S, R> FixedEncoding<@NonNull R> join(
            FixedEncoding<F> first,
            FixedEncoding<S> second,
            Bijection<@NonNull R, Tuple<F, S>> joiner) {
        return first.append(second, joiner);
    }

    /**
     * Returns a encoding which encodes nothing:
     * - The encoding function produces an empty byte array and
     * - The decoding function produces an object of the given type,
     *   as supplied by the {@code supplier} parameter
     *
     * Mirror of {@link Encoding#nullEncoding(CodingContext, Supplier)}.
     *
     * @param context the coding context that should be returned by the
     *                resulting Encodings {@link Encoding#getContext()}
     *                method.
     * @param supplier the function which supplies the values returned by
     *                 the resulting Encodings {@link Encoding#decode} function
     * @param <T> the type of value encoded by the resulting encoding
     * @return a new instance of {@link Encoding} which encodes nothing
     */
    public static <T> Encoding<@NonNull T> nullEncoding(CodingContext context, Supplier<@NonNull T> supplier) {
        return Encoding.nullEncoding(context, supplier);
    }
}
