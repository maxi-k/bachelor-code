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

public class EmittingTimeProvider implements TimeProvider {

    private static final AtomicReferenceFieldUpdater<EmittingTimeProvider, Duration> INTERVAL
            = AtomicReferenceFieldUpdater.newUpdater(EmittingTimeProvider.class, Duration.class, "interval");
    private volatile Duration interval;

    protected final Flux<Long> tickPublisher;

    @SuppressWarnings("initialization")
    protected EmittingTimeProvider(Duration initial, Publisher<Duration> durationProvider, Scheduler scheduler) {
        this.interval = initial;
        this.tickPublisher = Flux
                .switchOnNext(
                        Flux.from(durationProvider)
                        .doOnNext((dur) -> INTERVAL.set(this, dur))
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

    @Override
    public Duration getCurrentInterval() {
        return interval;
    }

    public EmittingTimeProvider create(Duration initial, Publisher<Duration> durationProvider, Scheduler scheduler) {
        return new EmittingTimeProvider(initial, durationProvider, scheduler);
    }

    public EmittingTimeProvider create(Duration initial, Publisher<Duration> durationProvider) {
        return create(initial, durationProvider, Schedulers.newSingle("EmittingTimeProvider Thread"));
    }

    public EmittingTimeProvider create(Duration initial, Publisher<Duration> durationProvider, Executor executor) {
        return create(initial, durationProvider, Schedulers.fromExecutor(executor));
    }

    public EmittingTimeProvider create(Duration duration, Scheduler schedule) {
        return create(duration, Mono.just(duration), schedule);
    }

    public EmittingTimeProvider create(Duration duration, Executor executor) {
        return create(duration, Schedulers.fromExecutor(executor));
    }

    public EmittingTimeProvider create(Duration duration) {
        return create(duration, Schedulers.newSingle("EmittingTimeProvider Thread"));
    }

}
