/* 2016 */
package de.unia.oc.robotcontrol.util;

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
}
