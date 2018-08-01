/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

abstract class MapMulticast<Topic, Value> implements FlowableMulticast<Topic, Value> {

    private final ConcurrentMap<@NonNull Topic, FluxProcessor<@NonNull Value, @NonNull Value>> multicastMap;
    private final FluxProcessor<@NonNull Value, @NonNull Value> mainProcessor;
    private final Flux<@NonNull Value> sideEffect;
    private final Scheduler scheduler;

    @SuppressWarnings("initialization")
    protected MapMulticast(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.multicastMap = createMulticastMap();
        this.mainProcessor = createMainProcessor();
        this.sideEffect = mainProcessor
                .doOnNext(this::dispatch)
                .subscribeOn(scheduler);
    }

    protected MapMulticast(Executor executor) {
        this(Schedulers.fromExecutor(executor));
    }

    @Override
    public Publisher<Value> subscribeTo(Topic topic) {
        return multicastMap.compute(topic, (t, current) -> {
            if (current == null || current.hasCompleted() || current.isTerminated() || current.isDisposed()) {
                return createTopicProcessor();
            }
            return current;
        }).publishOn(scheduler);
    }

    @Override
    public Processor<@NonNull Value, @NonNull Value> asProcessor() {
        return mainProcessor;
    }

    protected void dispatch(@NonNull Value value) {
        Topic topic = topicFromValue(value);
        synchronized (multicastMap) {
            if (multicastMap.containsKey(topic)) {
                multicastMap.get(topic).onNext(value);
            }
        }
    }

    protected ConcurrentMap<@NonNull Topic, FluxProcessor<@NonNull Value, @NonNull Value>> createMulticastMap() {
        return new ConcurrentHashMap<>();
    }

    protected abstract @NonNull Topic topicFromValue(Value value);

    protected abstract FluxProcessor<@NonNull Value, @NonNull Value> createMainProcessor();

    protected abstract FluxProcessor<@NonNull Value, @NonNull Value> createTopicProcessor();
}
