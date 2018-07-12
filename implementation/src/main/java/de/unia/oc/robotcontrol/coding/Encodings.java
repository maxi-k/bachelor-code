package de.unia.oc.robotcontrol.coding;

import de.unia.oc.robotcontrol.util.Bijection;
import de.unia.oc.robotcontrol.util.Tuple;

import java.util.function.Supplier;

public final class Encodings {


    public static <F, S, R> Encoding<R> stack(
            Encoding<F> bottom,
            Bijection<R, F> top) {
        return bottom.stack(top);
    }

    public static <F, S, R> Encoding<R> join(
            FixedEncoding<F> first,
            FixedEncoding<S> second,
            Bijection<R, Tuple<F, S>> joiner) {
        return first.append(second, joiner);
    }

    public static <T> Encoding<T> nullEncoding(CodingContext context, Supplier<T> supplier) {
        return Encoding.nullEncoding(context, supplier);
    }
}
