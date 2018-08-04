/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.flow.strategy.BufferFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.ReplayFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.SchedulingFlowStrategy;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.signedness.qual.Constant;
import org.checkerframework.dataflow.qual.Pure;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;

import java.util.concurrent.Executor;
import java.util.function.Predicate;

public abstract class FilteringMulticast<Topic extends Object, Value extends Object>
        implements ValueBoundMulticast<Topic, Value> {

    private final Scheduler scheduler;
    private final FluxProcessor<Value, Value> mainProcessor;

    public FilteringMulticast(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.mainProcessor = createProcessor();
    }

    public FilteringMulticast(Executor executor) {
        this(Schedulers.fromExecutor(executor));
    }

    public FilteringMulticast() {
        this(Schedulers.parallel());
    }

    @Override
    public Publisher<Value> subscribeTo(Topic topic) {
        return Flux
                .from(wrapSubscriptionProcessor(mainProcessor))
                .filter(valueFilteringPredicate(topic))
                .as(getFlowStrategy());
    }

    @Override
    public Processor<Value, Value> asProcessor() {
        return this.mainProcessor;
    }

    @Override
    public Publisher<Value> asPublisher() {
        return getFlowStrategy().apply(mainProcessor);
    }

    @Override
    public FlowStrategy<Value, Value> getFlowStrategy() {
        return SchedulingFlowStrategy
                .<Value>create(this.scheduler)
                .with(BufferFlowStrategy.create(topicBufferSize(), BufferOverflowStrategy.DROP_OLDEST));
    }

    protected FluxProcessor<Value, Value> createProcessor(@UnderInitialization FilteringMulticast<Topic, Value> this) {
        return TopicProcessor.create("FilteringMulticast", topicBufferSize());
    }

    protected Publisher<Value> wrapSubscriptionProcessor(FluxProcessor<Value, Value> publisher) {
        return publisher.share();
    }

    private Predicate<Value> valueFilteringPredicate(Topic topic) {
        return (v) -> areTopicsEqual(topic, topicFromValue(v));
    }

    protected boolean areTopicsEqual (Topic topic1, Topic topic2) {
        return topic1.equals(topic2);
    }

    @Pure
    @Constant
    protected int topicBufferSize(@UnknownInitialization FilteringMulticast<Topic, Value> this) {
        return 8;
    }
}
