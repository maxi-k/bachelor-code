/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.flow.function.SubscriberTransformation;
import de.unia.oc.robotcontrol.flow.strategy.LoggingFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.SchedulingFlowStrategy;
import de.unia.oc.robotcontrol.util.Logger;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.logging.Level;

public abstract class ReemittingMulticast<Topic extends Object, Value extends Object>
        implements ValueBoundMulticast<Topic, Value> {

    private final ConcurrentMap<Topic, FluxProcessor<Value, Value>> multicastMap;
    private final FluxProcessor<Value, Value> mainProcessor;
    private final Flux<Value> sideEffect;
    private final Scheduler scheduler;

    @SuppressWarnings("initialization")
    public ReemittingMulticast(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.multicastMap = createMulticastMap();
        this.mainProcessor = createMainProcessor();
        this.sideEffect = mainProcessor
                .doOnNext(this::dispatch)
                .subscribeOn(scheduler);
        this.sideEffect.subscribe();
    }

    public ReemittingMulticast(Executor executor) {
        this(Schedulers.fromExecutor(executor));
    }

    public ReemittingMulticast() {
        this(Schedulers.parallel());
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
    public Processor<Value, Value> asProcessor() {
        return mainProcessor;
    }

    @Override
    public Publisher<Value> asPublisher() {
        return getFlowStrategy().apply(mainProcessor.retry());
    }

    @Override
    public Subscriber<Value> asSubscriber() {
        return SubscriberTransformation.unboundedSubscription(
                SubscriberTransformation.anonymizeSubscription(mainProcessor)
        );
    }

    @Override
    public FlowStrategy<Value, Value> getFlowStrategy() {
        return SchedulingFlowStrategy.create(this.scheduler);
    }

    protected void dispatch(Value value) {
        Topic topic = topicFromValue(value);
        synchronized (multicastMap) {
            if (multicastMap.containsKey(topic)) {
                multicastMap.get(topic).onNext(value);
            } else {
                Logger.instance().debug("[Multicast] No subscriber found for topic " + topic);
            }
        }
    }

    protected ConcurrentMap<Topic, FluxProcessor<Value, Value>> createMulticastMap() {
        return new ConcurrentHashMap<>();
    }

    protected FluxProcessor<Value, Value> createMainProcessor() {
        return EmitterProcessor.create();
    }

    protected FluxProcessor<Value, Value> createTopicProcessor() {
        return EmitterProcessor.create();
    }
}
