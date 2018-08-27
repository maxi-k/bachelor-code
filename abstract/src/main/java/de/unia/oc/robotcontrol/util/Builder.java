/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.util.function.Function;

/**
 * Generic builder class used to build up values by continuously
 * transforming them.
 *
 * @param <T> The type of the value this builder holds
 */
public final class Builder<T> {

    private final T value;

    private Builder(T value) {
        this.value = value;
    }

    /**
     * Return a new builder with the held value transformed by
     * the given function
     * @param mapper the transforming function
     * @param <M> the type of the resulting value held in the new builder
     * @return A builder which holds the transformed value
     */
    public <M> Builder<M> map(Function<? super T, M> mapper) {
        return Builder.start(mapper.apply(this.value));
    }

    /**
     * Extract the value from the Builder instance.
     * Used at the end of the build with to get the actual
     * desired value.
     *
     * @return The {@link Builder#value} this Builder holds
     */
    public T get() {
        return value;
    }

    /**
     * Static constructor function
     *
     * @param value the value to wrap.
     * @param <T> the type of the value to wrap
     * @return An instance of Builder which wraps the given value.
     */
    public static <T> Builder<T> start(T value) {
        return new Builder<>(value);
    }

}
