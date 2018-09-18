/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * An implementation of {@link TimeProvider} which uses a
 * {@link Flux} to produce timed {@link Long} values.
 *
 * Same as {@link EmittingClock}, but the Duration cannot be
 * set explicitly using a {@link Clock#setInterval(Duration)} method.
 */
public class EmittingTimeProvider implements TimeProvider {

    /**
     * The publisher used to publish {@link Long} values on.
     */
    protected final Flux<Long> tickPublisher;

    /**
     * Creates a new {@link EmittingTimeProvider}, which uses a
     * {@link Publisher} of {@link Duration} classes and maps those
     * to ticks which are published in the interval determined
     * last published {@link Duration} instance.
     *
     * @param durationProvider the provider for {@link Duration} instances
     * @param scheduler the scheduler to run this Clock on
     */
    @SuppressWarnings("initialization")
    protected EmittingTimeProvider(Publisher<Duration> durationProvider, Scheduler scheduler) {
        this.tickPublisher = Flux
                .switchOnNext(
                        Flux.from(durationProvider)
                        .map(Flux::interval)
                        .onBackpressureLatest())
                .publishOn(scheduler)
                .publish()
                .autoConnect()
                .onBackpressureDrop();
    }

    @Override
    public Publisher<Long> getTicks() {
        return tickPublisher;
    }

    /**
     * Factory method mirroring {@link #EmittingTimeProvider(Publisher, Scheduler)}
     *
     * @param durationProvider the provider of durations to use as a basis
     *                         for the emitted ticks
     * @param scheduler the execution context to run this clock on
     * @return a new instance of {@link EmittingTimeProvider}
     */
    public EmittingTimeProvider create(Publisher<Duration> durationProvider, Scheduler scheduler) {
        return new EmittingTimeProvider(durationProvider, scheduler);
    }

    /**
     * Same as {@link #create(Publisher, Scheduler)}, but creates a new single
     * thread for this clock implicitly.
     *
     * @param durationProvider the provider of durations to use as a basis
     *                         for the emitted ticks
     * @return a new instance of {@link EmittingTimeProvider} which runs on its own thread
     */
    public EmittingTimeProvider create(Publisher<Duration> durationProvider) {
        return create(durationProvider, Schedulers.newSingle("EmittingTimeProvider Thread"));
    }

    /**
     * Same as {@link #create(Publisher, Scheduler)}, but runs on the given Executor.
     *
     * @param durationProvider the provider of durations to use as a basis
     *                         for the emitted ticks
     * @param executor the execution context to run this clock on
     * @return a new instance of {@link EmittingTimeProvider} which runs on its own thread
     */
    public EmittingTimeProvider create(Publisher<Duration> durationProvider, Executor executor) {
        return create(durationProvider, Schedulers.fromExecutor(executor));
    }

    /**
     * Same as {@link #create(Publisher, Scheduler)}, but runs on one single duration
     * constantly. Thus, there is now way of switching between durations
     *
     * @param duration the duration which sets the interval with which ticks are published
     * @param schedule the execution context to run this clock on
     * @return a new instance of {@link EmittingTimeProvider} which runs on its own thread
     */
    public EmittingTimeProvider create(Duration duration, Scheduler schedule) {
        return create(Mono.just(duration), schedule);
    }

    /**
     * Same as {@link #create(Publisher, Executor)}, but runs on one single duration
     * constantly. Thus, there is now way of switching between durations
     *
     * @param duration the duration which sets the interval with which ticks are published
     * @param executor the execution context to run this clock on
     * @return a new instance of {@link EmittingTimeProvider} which runs on its own thread
     */
    public EmittingTimeProvider create(Duration duration, Executor executor) {
        return create(duration, Schedulers.fromExecutor(executor));
    }

    /**
     * Same as {@link #create(Duration, Scheduler)}, but creates its own thread.
     * @param duration the duration which sets the interval with which ticks are published
     * @return a new instance of {@link EmittingTimeProvider} which runs on its own thread
     */
    public EmittingTimeProvider create(Duration duration) {
        return create(duration, Schedulers.newSingle("EmittingTimeProvider Thread"));
    }

}
