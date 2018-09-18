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

/**
 * A multicast which receives values using a {@link Processor} and
 * then re-emits them on a new processor for each {@link Topic} that
 * has been subscribed to (one processor per topic).
 *
 * @param <Topic> the type to use for topics
 * @param <Value> the type of values this receives and sends
 */
public abstract class ReemittingMulticast<Topic extends Object, Value extends Object>
        implements ValueBoundMulticast<Topic, Value> {

    /**
     * Maps {@link Topic} instances to Processors used for re-emitting
     */
    private final ConcurrentMap<Topic, FluxProcessor<Value, Value>> multicastMap;
    /**
     * The main processor which receives messages and puts them on
     * the other processors in the {@link #multicastMap}
     */
    private final FluxProcessor<Value, Value> mainProcessor;
    /**
     * The side-effect flux used to transfer values from the {@link #mainProcessor}
     * to the emitting processors using {@link #dispatch(Object)}
     */
    private final Flux<Value> sideEffect;
    /**
     * The execution context on which the dispatching an d re-emitting is executed.
     */
    private final Scheduler scheduler;

    /**
     * Creates a new Instance of {@link ReemittingMulticast} which
     * runs on the given Scheduler.
     *
     * @param scheduler the execution context to run on
     */
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

    /**
     * Creates a new Instance of {@link ReemittingMulticast} which
     * runs on the given Executor.
     *
     * @param executor the execution context to run on
     */
    public ReemittingMulticast(Executor executor) {
        this(Schedulers.fromExecutor(executor));
    }

    /**
     * Creates a new Instance of {@link ReemittingMulticast} which
     * runs in a dynamically parallel execution context.
     */
    public ReemittingMulticast() {
        this(Schedulers.parallel());
    }

    /**
     * {@inheritDoc}
     *
     * Modifies the {@link #multicastMap}, inserting a new processor
     * using {@link #createTopicProcessor()} if there has been no
     * subscription to that topic yet.
     *
     * Ensures the processor publishes on {@link #scheduler}.
     *
     * @param topic the {@link Topic} to subscribe to
     * @return an instance of {@link Publisher} which emits
     * values bound to the given topic with {@link #topicFromValue(Object)}
     */
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

    /**
     * {@inheritDoc}
     *
     * Applies the {@link FlowStrategy} from {@link #getFlowStrategy()} to the
     * {@link #mainProcessor}, and adds a retry mechanism, so that there is
     * a re-subscription when errors occur.
     *
     * @return an instance of {@link Publisher}
     */
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

    /**
     * Dispatches the given value to the correct processor
     * stored in {@link #multicastMap}, where the correct
     * topic is determined from {@link #topicFromValue(Object)}
     * applied to the passed value. If there is no matching
     * re-emitting processor in the {@link #multicastMap}, simply
     * logs a debug message.
     *
     * @param value the value to dispatch
     */
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

    /**
     * Initially create the Map used as {@link #multicastMap}.
     * Can be overridden in subclasses to use a different map implementation.
     * Uses a {@link ConcurrentHashMap} by default.
     *
     * @return a new instance of {@link ConcurrentMap}
     */
    protected ConcurrentMap<Topic, FluxProcessor<Value, Value>> createMulticastMap() {
        return new ConcurrentHashMap<>();
    }

     /**
      * Initially create the Processor used as {@link #mainProcessor}.
      * Can be overridden in subclasses to use a different processor implementation.
      * Uses a {@link EmitterProcessor} by default.
      *
      * @return a new instance of {@link FluxProcessor}
      */
    protected FluxProcessor<Value, Value> createMainProcessor() {
        return EmitterProcessor.create();
    }

    /**
     * Create the processors used in the {@link #multicastMap} for
     * re-emitting the values.
     * Can be overridden in subclasses to use a different processor implementation.
     * Uses a {@link EmitterProcessor} by default.
     *
     * @return a new instance of {@link FluxProcessor}
     */
    protected FluxProcessor<Value, Value> createTopicProcessor() {
        return EmitterProcessor.create();
    }
}
