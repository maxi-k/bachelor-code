/* 2016 */
package de.unia.oc.robotcontrol.util;

import java.util.function.Function;

public final class Builder<T> {

    private final T value;

    private Builder(T value) {
        this.value = value;
    }

    public <M> Builder<M> map(Function<T, M> mapper) {
        return Builder.start(mapper.apply(this.value));
    }

    public T get() {
        return value;
    }

    public static <T> Builder<T> start(T value) {
        return new Builder<>(value);
    }

}
