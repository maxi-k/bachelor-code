package de.unia.oc.robotcontrol.coding;

import de.unia.oc.robotcontrol.util.Bijection;
import de.unia.oc.robotcontrol.util.Tuple;

public final class Encodings {

    public static <F, S, R> Encoding<R> join(
            FixedEncoding<F> first,
            FixedEncoding<S> second,
            Bijection<Tuple<F, S>, R> joiner) {
        return first.append(second, joiner);
    }
}
