/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.flow.function.PublisherTransformation;
import org.checkerframework.dataflow.qual.Pure;
import org.reactivestreams.Publisher;

import java.util.function.Function;

/**
 * A {@link FlowStrategy} is a transformation of a {@link Publisher} that implies different
 * effects of the flow pipeline.
 *
 * These effects are categorized in {@link FlowStrategyType}.
 *
 * @param <T> the type which is streamed by the input publisher
 * @param <R> the type which will be streamed by the output publisher
 */
public interface FlowStrategy<T extends Object, R extends Object>
        extends PublisherTransformation<T, R> {

    String PROPERTY_NAME = "flowStrategy";

    /**
     * Return the type of effect this has on the program flow
     * as categorized by {@link FlowStrategyType}.
     *
     * Should remain constant for each instance of {@link FlowStrategy}
     *
     * @return an instance of {@link FlowStrategyType}
     */
    @Pure FlowStrategyType getType();

    /**
     * Return a {@link FlowStrategy} instance that applies the given
     * publisher transformation after itself.
     * Assert that the resulting effect on the program flow will be
     * that of the given {@link FlowStrategyType}.
     *
     * Corresponds to {@link Function#andThen(Function)}
     *
     * @param other the transformation to apply after
     * @param resultingType the {@link FlowStrategyType} the resulting
     *                      strategy is asserted to have
     * @param <S> the type of data given transformation emits
     * @return a new instance of {@link FlowStrategy}
     */
    default <S extends Object> FlowStrategy<T, S> with(
            Function<? super Publisher<R>, ? extends Publisher<S>> other,
            FlowStrategyType resultingType
    ) {
        FlowStrategy<T, R> self = this;
        return new FlowStrategy<T, S>() {
            @Override
            public FlowStrategyType getType() {
                return resultingType;
            }

            @Override
            public Publisher<S> apply(Publisher<T> next) {
                return self.andThen(other).apply(next);
            }
        };
    }

    /**
     * Return a {@link FlowStrategy} instance that applies the given
     * publisher transformation after itself.
     * Assert that the resulting effect on the program flow will be
     * the same as {@link #getType()} of this {@link FlowStrategy}
     *
     * Corresponds to {@link Function#andThen(Function)}
     *
     * @param other the transformation to apply after
     * @param <S> the type of data given transformation emits
     * @return a new instance of {@link FlowStrategy}
     */
    default <S extends Object> FlowStrategy<T, S> with(Function<? super Publisher<R>, ? extends Publisher<S>> other) {
        return with(other, this.getType());
    }

    /**
     * Return a {@link FlowStrategy} instance which applies the
     * same transformation as this one, but asserting that its
     * {@link FlowStrategyType} is the given type.
     *
     * @param type the {@link FlowStrategyType} that will be
     *            returned from {@link #getType()} on the
     *             resulting {@link FlowStrategy}
     * @return a new instance of {@link FlowStrategy}
     */
    default FlowStrategy<T, R> assertType(FlowStrategyType type) {
        FlowStrategy<T, R> self = this;
        return new FlowStrategy<T, R>() {
            @Override
            public FlowStrategyType getType() {
                return type;
            }

            @Override
            public Publisher<R> apply(Publisher<T> publisher) {
                return self.apply(publisher);
            }
        };
    }

    /**
     * Concatenate the two given {@link FlowStrategy} instances together,
     * applying the effects of the second one after the first one.
     *
     * Alias for <code>first.with(second)</code>.
     *
     * @param first the {@link FlowStrategy} to apply first
     * @param second the {@link FlowStrategy} to apply second
     * @param <S> the type that the resulting strategy will accept
     * @param <T> the intermediary type that the first strategy emits
     *           and the second strategy accepts
     * @param <R> th etype that the resulting strategy will accept
     * @return a new instance of {@link FlowStrategy}
     */
    static <S extends Object, T extends Object, R extends Object> FlowStrategy<S, R> concat(
            FlowStrategy<S, T> first,
            FlowStrategy<T, R> second
    ) {
            return first.with(second);
    }

    /**
     * Concatenate the two given {@link FlowStrategy} instances together,
     * applying the effects of the second one after the first one.
     *
     * Alias for <code>first.with(second, strategy)</code>.
     *
     * @param first the {@link FlowStrategy} to apply first
     * @param second the {@link FlowStrategy} to apply second
     * @param strategy the {@link FlowStrategyType} the resulting strategy will have
     * @param <S> the type that the resulting strategy will accept
     * @param <T> the intermediary type that the first strategy emits
     *           and the second strategy accepts
     * @param <R> th etype that the resulting strategy will accept
     * @return a new instance of {@link FlowStrategy}
     */
    static <S extends Object, T extends Object, R extends Object> FlowStrategy<S, R> concat(
            FlowStrategy<S, T> first,
            FlowStrategy<T, R> second,
            FlowStrategyType strategy
    ) {
        return first.with(second, strategy);
    }
}
