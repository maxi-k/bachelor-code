/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.reactivestreams.Publisher;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class EmittingClock implements Clock {

    private static final AtomicReferenceFieldUpdater<EmittingClock, Duration> INTERVAL
            = AtomicReferenceFieldUpdater.newUpdater(EmittingClock.class, Duration.class, "interval");
    private volatile Duration interval;

    private final FluxProcessor<Duration, Duration> durationProcessor;
    private final Flux<Long> tickPublisher;

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

    public static EmittingClock create(Duration interval) {
        return new EmittingClock(interval, Schedulers.newSingle("EmittingClock Thread"));
    }

    public static EmittingClock create(Duration interval, Scheduler scheduler) {
        return new EmittingClock(interval, scheduler);
    }

    public static EmittingClock create(Duration interval, Executor executor) {
        return new EmittingClock(interval, Schedulers.fromExecutor(executor));
    }

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
}
