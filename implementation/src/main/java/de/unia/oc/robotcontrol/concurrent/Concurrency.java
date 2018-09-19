/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Class with utility methods for the concurrent module of
 * the project ({@link de.unia.oc.robotcontrol.concurrent})
 * ("module-class").
 *
 * This only contains static methods and cannot be instantiated.
 */
public final class Concurrency {

    private Concurrency() {}

    /**
     * Mirror of {@link ProcessingClockState#create(Supplier, BiFunction)}
     * @param initialInput a supplier for the initial input, which is used if a tick happens
     *                     on the clock when there has been no input to the {@link Clockable}
     *                     instance to pass to the merging function
     * @param mergingFunction the function used to combine a tick emitted by the Clock with
     *                        the last value from the {@link Clockable} {@link org.reactivestreams.Publisher}
     * @param <T> the type of input the {@link ClockState}s {@link de.unia.oc.robotcontrol.flow.FlowStrategy}
     *           accepts
     * @param <R> the type of output the {@link ClockState}s {@link de.unia.oc.robotcontrol.flow.FlowStrategy}
     *           generates
     * @return a new instance of {@link ProcessingClockState}
     */
    public static <T extends Object, R extends Object> ProcessingClockState<T, R> createClockState(
            Supplier<T> initialInput,
            BiFunction<Long, T, R> mergingFunction
    ) {
        return ProcessingClockState.create(initialInput, mergingFunction);
    }

    /**
     * Mirror of {@link IgnoringClockState#create(Clockable.ClockType)}, using
     * the unclocked clock type.
     * @param <T> the generic type the{@link ClockState} will have
     * @return a new instance of {@link IgnoringClockState}
     */
    public static <T extends Object> IgnoringClockState<T> createUnclockedClockState() {
        return IgnoringClockState.create(Clockable.ClockType.UNCLOCKED);
    }

    /**
     * Mirror of {@link IgnoringClockState#create(Clockable.ClockType)}, using
     * the internal clock type.
     * @param <T> the generic type the{@link ClockState} will have
     * @return a new instance of {@link IgnoringClockState}
     */
    public static <T extends Object> IgnoringClockState<T> createInternalClockState() {
        return IgnoringClockState.create(Clockable.ClockType.INTERNAL);
    }

    /**
     * Mirror of {@link EmittingClock#create(Duration)}.
     *
     * @param interval the interval to emit values on initially
     * @return a new instance of {@link EmittingClock} running on the
     * given interval
     */
    public static EmittingClock createClock(Duration interval) {
        return EmittingClock.create(interval);
    }

    /**
     * Mirror of {@link EmittingClock#create(Duration, Executor)}.
     * @param interval the interval to emit values on initially
     * @param executor the execution context to run the clock on
     * @return a new {@link EmittingClock} instance running on the given
     * interval an scheduler
     */
    public static EmittingClock createClock(Duration interval, Executor executor) {
        return EmittingClock.create(interval, executor);
    }
}
