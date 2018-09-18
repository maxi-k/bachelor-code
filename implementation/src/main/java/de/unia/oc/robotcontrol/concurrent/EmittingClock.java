/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.reactivestreams.Publisher;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * A basic {@link Clock} implementation which uses a {@link EmitterProcessor}
 * to receive {@link Duration} elements, setting the internally used duration
 * to it atomically, and then updates the processor to emit values in the given
 * interval.
 */
public class EmittingClock implements Clock {

    /**
     * The field updater used for updating the {@link #interval} field atomically
     * in the {@link #setInterval(Duration)} method.
     */
    private static final AtomicReferenceFieldUpdater<EmittingClock, Duration> INTERVAL
            = AtomicReferenceFieldUpdater.newUpdater(EmittingClock.class, Duration.class, "interval");
    /**
     * The currently set duration which specifies the interval with which
     * {@link Long}s are emitted by this clock.
     */
    private volatile Duration interval;

    /**
     * The Instance of {@link FluxProcessor} used as input for Duration elements
     * when they are set by {@link #setInterval(Duration)}. Converted to
     * something that emits {@link Long}s in that interval with {@link #tickPublisher}.
     */
    private final FluxProcessor<Duration, Duration> durationProcessor;
    /**
     * Conversion of {@link #durationProcessor} which emits values based on last
     * interval that was passed to the processor as input.
     */
    private final Flux<Long> tickPublisher;

    /**
     * Creates a new Instance of {@link EmittingClock}, which publishes values
     * and executes calculations on the execution context given by the Scheduler,
     * resulting in a {@link Publisher} as returned by {@link #getTicks()} that
     * emits values in the given interval.
     *
     * @param interval the interval to emit values on initially
     * @param scheduler the execution context to run the scheduling on
     */
    private EmittingClock(Duration interval, Scheduler scheduler) {
        this.interval = interval;

        this.durationProcessor = EmitterProcessor.create();
        this.tickPublisher = Flux
                .switchOnNext(durationProcessor.map(Flux::interval).onBackpressureLatest())
                .publishOn(scheduler)
                .publish()
                .autoConnect()
                .onBackpressureDrop();

        this.durationProcessor.onNext(interval);
    }

    /**
     * {@inheritDoc}
     *
     * Sets the current interval atomically, and informs the {@link #durationProcessor}
     * of the newly set interval, so it can switch its publishing duration.
     * @param interval the interval to eventually switch to
     */
    @Override
    public void setInterval(Duration interval) {
        INTERVAL.set(this, interval);
        durationProcessor.onNext(interval);
    }

    @Override
    public Publisher<Long> getTicks() {
        return tickPublisher;
    }

    @Override
    public Duration getCurrentInterval() {
        return interval;
    }

    /**
     * Factory function for the default constructor {@link #EmittingClock(Duration, Scheduler)}
     * @param interval the initially used interval
     * @param scheduler the scheduler to publish values on
     * @return a new Instance of {@link EmittingClock}
     */
    public static EmittingClock create(Duration interval, Scheduler scheduler) {
        return new EmittingClock(interval, scheduler);
    }

    /**
     * Same as {@link #create(Duration, Scheduler)}, but implicitly creates
     * a new Thread to run the clock on.
     *
     * @param interval the initial duration to use as publishing interval
     * @return a new Instance of {@link EmittingClock} running on its own thread
     */
    public static EmittingClock create(Duration interval) {
        return create(interval, Schedulers.newSingle("EmittingClock Thread"));
    }

    /**
     * Same as {@link #create(Duration, Scheduler)}, but creates a scheduler
     * from the given executor.
     *
     * @param interval the initial duration to use as publishing interval
     * @param executor the executor to run the new Clock on
     * @return a new Instance of {@link EmittingClock} running on the given executor
     */
    public static EmittingClock create(Duration interval, Executor executor) {
        return create(interval, Schedulers.fromExecutor(executor));
    }
}
