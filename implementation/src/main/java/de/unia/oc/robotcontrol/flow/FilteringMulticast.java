/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.flow.strategy.BufferFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.SchedulingFlowStrategy;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.signedness.qual.Constant;
import org.checkerframework.dataflow.qual.Pure;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.TopicProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;
import java.util.function.Predicate;

/**
 * An implementation of multicast which uses a single processor to receive messages,
 * which are not processed in any way. Instead, {@link #subscribeTo(Object)} simply returns
 * a new Publisher with a filter on it, filtering messages that belong to the subscribed-to
 * topic. This only works if the topic can be derived from the message traveling along the
 * multicast, which is why this is an instance of {@link ValueBoundMulticast}.
 *
 * @param <Topic> the type to use for topics
 * @param <Value> the type of values this receives and sends
 */
public abstract class FilteringMulticast<Topic extends Object, Value extends Object>
        implements ValueBoundMulticast<Topic, Value> {

    /**
     * The execution context this runs on
     */
    private final Scheduler scheduler;
    /**
     * The processor used to receive and send out messages
     */
    private final FluxProcessor<Value, Value> mainProcessor;

    /**
     * Creates a new instance of {@link FilteringMulticast} which
     * runs on the passed scheduler. Creates the internal processor
     * using {@link #createProcessor()}.
     *
     * @param scheduler the execution context this should run on
     */
    public FilteringMulticast(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.mainProcessor = createProcessor();
    }

    /**
     * Creates a new instance of {@link FilteringMulticast} which
     * runs on the passed Executor. Calls the main constructor
     * {@link #FilteringMulticast(Scheduler)}
     * @param executor
     */
    public FilteringMulticast(Executor executor) {
        this(Schedulers.fromExecutor(executor));
    }

    /**
     * Creates a new instance of {@link FilteringMulticast} which
     * runs on a dynamically parallel scheduler.
     */
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

    /**
     * Create the processor for {@link #mainProcessor}. Can be overridden in
     * subclasses to allow usage of a different processor. The default
     * is {@link TopicProcessor}.
     * @return a new instance of {@link FluxProcessor}
     */
    protected FluxProcessor<Value, Value> createProcessor(@UnderInitialization FilteringMulticast<Topic, Value> this) {
        return TopicProcessor.create("FilteringMulticast", topicBufferSize());
    }

    /**
     * Transforms the passed processor, returning a Publisher.
     * Used in {@link #subscribeTo(Object)} to transform the Processor
     * before applying the topic filter (using {@link #valueFilteringPredicate(Object)}
     * and returning it to the caller.
     *
     * The default behavior is to call {@link FluxProcessor#share()} to ensure
     * multiple subscriptions are allowed. Can be overridden in subclasses to alter behavior.
     *
     * @param publisher the processor to transform
     * @return an instance of {@link Publisher}
     */
    protected Publisher<Value> wrapSubscriptionProcessor(FluxProcessor<Value, Value> publisher) {
        return publisher.share();
    }

    /**
     * Create Predicate used for filtering values based on a specific topic.
     * Uses the {@link #areTopicsEqual(Object, Object)} function to
     * compare the given topic with the one retrieved from the value transferring
     * through the flux using {@link #topicFromValue(Object)}.
     * Used in {@link #subscribeTo(Object)} to dynamically generate a predicate.
     *
     * @param topic the topic to base the predicate off of
     * @return an instance of {@link Predicate}
     */
    private Predicate<Value> valueFilteringPredicate(Topic topic) {
        return (v) -> areTopicsEqual(topic, topicFromValue(v));
    }

    /**
     * Compare two instances of {@link Topic} to determine whether they are equal.
     * Uses the {@link Object#equals(Object)} method by default.
     * Used in {@link #valueFilteringPredicate(Object)}
     *
     * @param topic1 the first topic to compare
     * @param topic2 the second topic to compare to the first one
     * @return whether the two topics are to be considered equal
     */
    protected boolean areTopicsEqual (Topic topic1, Topic topic2) {
        return topic1.equals(topic2);
    }

    /**
     * Buffer size used in {@link #subscribeTo(Object)} to buffer the returned
     * Publisher using {@link BufferFlowStrategy}. The default is 8.
     *
     * @return the size of the buffer to use for Publishers returned by {@link #subscribeTo(Object)}
     */
    @Pure
    @Constant
    protected int topicBufferSize(@UnknownInitialization FilteringMulticast<Topic, Value> this) {
        return 8;
    }
}
