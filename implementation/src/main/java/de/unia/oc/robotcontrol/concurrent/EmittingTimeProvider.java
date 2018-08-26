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

    protected final Flux<Long> tickPublisher;

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

    public EmittingTimeProvider create(Publisher<Duration> durationProvider, Scheduler scheduler) {
        return new EmittingTimeProvider(durationProvider, scheduler);
    }

    public EmittingTimeProvider create(Publisher<Duration> durationProvider) {
        return create(durationProvider, Schedulers.newSingle("EmittingTimeProvider Thread"));
    }

    public EmittingTimeProvider create(Publisher<Duration> durationProvider, Executor executor) {
        return create(durationProvider, Schedulers.fromExecutor(executor));
    }

    public EmittingTimeProvider create(Duration duration, Scheduler schedule) {
        return create(Mono.just(duration), schedule);
    }

    public EmittingTimeProvider create(Duration duration, Executor executor) {
        return create(duration, Schedulers.fromExecutor(executor));
    }

    public EmittingTimeProvider create(Duration duration) {
        return create(duration, Schedulers.newSingle("EmittingTimeProvider Thread"));
    }

}
