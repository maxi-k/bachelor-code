/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.util.function.Function;

/**
 * Reverse of {@link Builder}.
 * Impractical due to restrictions on the java type system, will probably be removed.
 *
 * @param <I> the type of the input element
 * @param <O> the type of the output element
 */
@Deprecated
public final class Wrapper<I, O> {

    private final Function<? super I, O> constructor;

    private Wrapper(Function<? super I, O> constructor) {
        this.constructor = constructor;
    }

    public <N> Wrapper<I, N> with(Function<? super O, N> next) {
        return new Wrapper<>(constructor.andThen(next));
    }

    public O get(I arg) {
        return constructor.apply(arg);
    }

    public static <I, O> Wrapper<I, O> start(Function<I, O> constructor) {
        return new Wrapper<>(constructor);
    }
}
