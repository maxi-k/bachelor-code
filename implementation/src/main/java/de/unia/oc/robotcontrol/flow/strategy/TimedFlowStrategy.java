/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Implementation of {@link FlowStrategy} used for ensuring a certain
 * minimal interval between values on a given publisher. Uses another
 * {@link Publisher} of Longs to generate a stream of values published
 * at a certain interval, then merges that Publisher with the Publisher
 * to be transformed using a merging function.
 *
 * @param <Input> the type of object published by the Publisher
 *           which is to be transformed by this flow strategy
 * @param <Output> the type of object published by the transformed Publisher
 */
public class TimedFlowStrategy<Input extends Object, Output extends Object> implements FlowStrategy<Input, Output> {

    /**
     * The publisher used as a timer
     */
    private final Publisher<Long> timer;
    /**
     * Supplies the initial input for the {@link #mergingFunction} if
     * the first tick occurs before the first input from the transformed
     * {@link Publisher}
     */
    private final Supplier<Input> initialInput;
    /**
     * The function used to merge the ticks {@link Long} with the
     * values published by the transformed publisher.
     */
    private final BiFunction<Long, Input, Output> mergingFunction;

    /**
     * Create a new instance of {@link TimedFlowStrategy} using the given {@link Publisher}
     * as {@link #timer}, the given {@link Supplier} as {@link #initialInput},
     * and the given {@link BiFunction} as {@link #mergingFunction}.
     *
     * @param timer the timer used for the ticks
     * @param initialInput the provider of the initial input
     * @param mergingFunction the function used to merge the tick with
     *                        the values published by the transformed Publisher
     */
    private TimedFlowStrategy(Publisher<Long> timer,
                              Supplier<Input> initialInput,
                              BiFunction<Long, Input, Output> mergingFunction) {
        this.timer = timer;
        this.initialInput = initialInput;
        this.mergingFunction = mergingFunction;
    }

    /**
     * Create a new instance of {@link TimedFlowStrategy} using the given {@link Publisher}
     * as {@link #timer}, the given {@link Supplier} as {@link #initialInput},
     * and the given {@link BiFunction} as {@link #mergingFunction}.
     * Mirror of {@link #TimedFlowStrategy(Publisher, Supplier, BiFunction)}.
     *
     * @param timer the timer used for the ticks
     * @param initialInput the provider of the initial input
     * @param mergingFunction the function used to merge the tick with
     *                        the values published by the transformed Publisher
     */
    public static <I, O> TimedFlowStrategy<I, O> createTimed(Publisher<Long> timer,
                                                             Supplier<I> initialInput,
                                                             BiFunction<Long, I, O> mergingFunction) {
        return new TimedFlowStrategy<>(timer, initialInput, mergingFunction);
    }

    /**
     * Create a new instance of {@link TimedFlowStrategy}, using the given {@link Publisher} as
     * {@link #timer} and the given {@link Supplier} as {@link #initialInput}.
     * The merging function ignores the tick and just returns the last know value from
     * the transformed Publisher. It is thus given that some values are repeated.
     *
     * @param timer the timer used for the ticks
     * @param initialInput the provider of the initial input
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link TimedFlowStrategy}
     */
    public static <T> TimedFlowStrategy<T, T> createTimed(Publisher<Long> timer, Supplier<T> initialInput) {
        return createTimed(timer, initialInput, (l, t) -> t);
    }

    /**
     * Creates a new instance of {@link TimedFlowStrategy}, using a fixed {@link Duration} to
     * generate a Publisher of Ticks conforming to the definition {@link #timer} which generates
     * Long values in the given interval. Uses the rest of the values like
     * {@link #createTimed(Publisher, Supplier, BiFunction)}.
     *
     * @param duration the duration to use as the interval to publish values in
     * @param initialInput the provider of the initial input
     * @param mergingFunction the function used to merge the tick with
     *                        the values published by the transformed Publisher
     * @param <I> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @param <O> the type of object published by the transformed Publisher,
     *           as returned by the merging function.
     * @return a new instance of {@link TimedFlowStrategy} which uses a fixed Duration
     */
    public static <I, O> TimedFlowStrategy<I, O> createDurational(Duration duration, Supplier<I> initialInput, BiFunction<Long, I, O> mergingFunction) {
        return createTimed(Flux.interval(duration), initialInput, mergingFunction);
    }

    /**
     * Create a new instance of {@link TimedFlowStrategy} using the given
     * {@link Supplier} as {@link #initialInput}, and the given {@link BiFunction}
     * as {@link #mergingFunction}. The given {@link Supplier} supplies Instances
     * of {@link Duration}, and the newest supplied {@link Duration} instance ist
     * used to switch the {@link #timer} to produce values at that specific interval.
     *
     * @param durationSupplier the supplier for the durations used to switch the
     *                         interval of longs published on the resulting timer
     * @param initialInput the provider of the initial input
     * @param mergingFunction the function used to merge the tick with
     *                        the values published by the transformed Publisher
     * @param <I> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @param <O> the type of object published by the transformed Publisher,
     *           as returned by the merging function.
     * @return a new instance of {@link TimedFlowStrategy} which uses a the given
     * {@link Supplier} to switch interval times.
     */
    public static <I, O> TimedFlowStrategy<I, O> createDurational(Publisher<Duration> durationSupplier,
                                                                  Supplier<I> initialInput,
                                                                  BiFunction<Long, I, O> mergingFunction) {
        return createTimed(
                Flux.switchOnNext(Flux.from(durationSupplier).map(Flux::interval)),
                initialInput,
                mergingFunction);
    }

    /**
     * Like {@link #createDurational(Publisher, Supplier, BiFunction)}, but ignores the {@link Long}
     * values produced by the tick publisher and just returns the last known value produced
     * by the transformed Publisher, similarly to the {@link TimedFlowStrategy} returned
     * by {@link #createTimed(Publisher, Supplier)}.
     *
     * @param durationSupplier the supplier for the durations used to switch the
     *                         interval of longs published on the resulting timer
     * @param initialInput the provider of the initial input
     *
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link TimedFlowStrategy}
     */
    public static <T> TimedFlowStrategy<T, T> createDurational(Publisher<Duration> durationSupplier, Supplier<T> initialInput) {
        return createTimed(Flux.switchOnNext(Flux.from(durationSupplier).map(Flux::interval)), initialInput);
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.TRANSPARENT;
    }

    @Override
    public Publisher<Output> apply(Publisher<Input> publisher) {
        return Flux.combineLatest(
                timer,
                Flux.from(publisher).startWith(initialInput.get()),
                mergingFunction);
    }
}
