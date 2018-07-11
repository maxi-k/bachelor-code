/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class Tuple<F, S> {

    public final F first;
    public final S second;

    public Tuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public <N> Tuple<N, S> mapFirst(Function<F, N> fn) {
        return new Tuple<>(fn.apply(first), second);
    }

    public <N> Tuple<F, N> mapSecond(Function<S, N> fn) {
        return new Tuple<>(first, fn.apply(second));
    }

    public <NF, NS> Tuple<NF, NS> map(Function<F, NF> fn1, Function<S, NS> fn2) {
        return new Tuple<>(fn1.apply(first), fn2.apply(second));
    }

    public <R> R joinWith(Function<Tuple<F, S>, R> joiner) {
        return joiner.apply(this);
    }

    public <R> R joinWith(BiFunction<F, S, R> joiner) {
        return joiner.apply(first, second) ;
    }

    public static <F, S, R> Tuple<F, S> split(R value, Function<R, Tuple<F, S>> splitter) {
        return splitter.apply(value);
    }

    public static <F, S> Tuple<F, S> create(F first, S second) {
        return new Tuple(first, second);
    }
}
