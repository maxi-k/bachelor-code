/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

abstract class MapMulticast<Topic extends Object, Value extends Object>
        implements FlowableMulticast<Topic, Value> {

    private final ConcurrentMap<Topic, FluxProcessor<Value, Value>> multicastMap;
    private final FluxProcessor<Value, Value> mainProcessor;
    private final Flux<Value> sideEffect;
    private final Scheduler scheduler;

    @SuppressWarnings("initialization")
    protected MapMulticast(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.multicastMap = createMulticastMap();
        this.mainProcessor = createMainProcessor();
        this.sideEffect = mainProcessor
                .doOnNext(this::dispatch)
                .subscribeOn(scheduler);
        this.sideEffect.subscribe();
    }

    protected MapMulticast(Executor executor) {
        this(Schedulers.fromExecutor(executor));
    }

    @Override
    public  Publisher<Value> subscribeTo(Topic topic) {
        return multicastMap.compute(topic, (t, current) -> {
            if (current == null || current.hasCompleted() || current.isTerminated() || current.isDisposed()) {
                return createTopicProcessor();
            }
            return current;
        }).publishOn(scheduler);
    }

    @Override
    public Processor<Value, Value> asProcessor() {
        return mainProcessor;
    }

    protected void dispatch(Value value) {
        Topic topic = topicFromValue(value);
        synchronized (multicastMap) {
            if (multicastMap.containsKey(topic)) {
                multicastMap.get(topic).onNext(value);
            }
        }
    }

    protected ConcurrentMap<Topic, FluxProcessor<Value, Value>> createMulticastMap() {
        return new ConcurrentHashMap<>();
    }

    protected abstract Topic topicFromValue(Value value);

    protected abstract FluxProcessor<Value,  Value> createMainProcessor();

    protected abstract FluxProcessor<Value,  Value> createTopicProcessor();
}
