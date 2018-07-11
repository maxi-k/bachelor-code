/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A Tuple that wraps two arbitrary values of a concrete type,
 * and defines various transformations.
 * May be written as (F, S) in documentation.
 *
 * @param <F> the Type of the (F)irst element
 * @param <S> the Type of the (S)econd element
 */
public final class Tuple<F, S> {

    public final F first;
    public final S second;

    public Tuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Applies the given function to the first element in the tuple,
     * returning a new tuple with the transformed element.
     *
     * @param fn the function to apply to the first element
     * @param <N> the Type of the transformed element
     * @return A new Tuple of Type (N, S)
     */
    public <N> Tuple<N, S> mapFirst(Function<F, N> fn) {
        return new Tuple<>(fn.apply(first), second);
    }

    /**
     * Applies the given function to the second element in the tuple,
     * returning a new tuple with the transformed element.
     *
     * @param fn the function to apply to the second element
     * @param <N> the type of the transformed element
     * @return A new Tuple of type (N, S)
     */
    public <N> Tuple<F, N> mapSecond(Function<S, N> fn) {
        return new Tuple<>(first, fn.apply(second));
    }

    /**
     * Applies the given functions to the first and the second element of
     * the tuple, respectively, returning a new tuple which holds the
     * transformed values.
     *
     * @param fn1 the function to apply to the first element.
     * @param fn2 the function to apply to the second element.
     * @param <NF> the type of the transformed first element
     * @param <NS> the type of the transformed second element
     * @return A new Tuple of type (NF, NS)
     */
    public <NF, NS> Tuple<NF, NS> map(Function<F, NF> fn1, Function<S, NS> fn2) {
        return new Tuple<>(fn1.apply(first), fn2.apply(second));
    }

    /**
     * Returns a new arbitrary value by applying the given joiner
     * function to the tuple.
     * @param joiner the function to apply to the tuple
     * @param <R> the type of the resulting element
     * @return Some object returned by applying the joiner function to this tuple
     */
    public <R> R joinWith(Function<Tuple<F, S>, R> joiner) {
        return joiner.apply(this);
    }

    /**
     * Returns a new arbitrary value by applying the given joiner
     * (bi-)function to the first- and second value of the tuple.
     * @param joiner the function to apply to the values of tuple
     * @param <R> the type of the resulting element
     * @return Some object returned by applying the joiner function to the values of this tuple
     */
    public <R> R joinWith(BiFunction<F, S, R> joiner) {
        return joiner.apply(first, second) ;
    }

    /**
     * Splits the given value by applying the splitter function to it,
     * returning a Tuple.
     *
     * @param value the value to split
     * @param splitter the function with which to split the value
     * @param <F> the type of the first element of the resulting tuple
     * @param <S> the type of the second element of the resulting tuple
     * @param <R> the type of the input element to be split
     * @return A tuple of type (F, S)
     */
    public static <F, S, R> Tuple<F, S> split(R value, Function<R, Tuple<F, S>> splitter) {
        return splitter.apply(value);
    }

    /**
     * Static constructor function. Creates a new tuple out of the
     * given parameters.
     *
     * @param first the first value of the resulting tuple
     * @param second the second value of the resulting tuple
     * @param <F> the type of the first value of the resulting tuple
     * @param <S> the type of the second value of the resulting tuple
     * @return A new tuple of type (F, S)
     */
    public static <F, S> Tuple<F, S> create(F first, S second) {
        return new Tuple<>(first, second);
    }
}
