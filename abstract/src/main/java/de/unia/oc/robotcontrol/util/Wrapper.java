/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.util.function.Function;

public final class Wrapper<I, O> {

    private final Function<I, O> constructor;

    private Wrapper(Function<I, O> constructor) {
        this.constructor = constructor;
    }

    public <N> Wrapper<I, N> with(Function<O, N> next) {
        return new Wrapper<>(constructor.andThen(next));
    }

    public O get(I arg) {
        return constructor.apply(arg);
    }

    public static <I, O> Wrapper<I, O> start(Function<I, O> constructor) {
        return new Wrapper<>(constructor);
    }
}
