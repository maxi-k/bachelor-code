/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

/**
 * A special {@link FlowableProcessor} used to dynamically multicast
 * data to multiple subscribers.
 *
 * This may break the backpressure throughout the Flow pipeline,
 * as a 'only forward the minimal amount requested for all subscribers of a topic'
 * strategy is not always acceptable.
 * Thus, subscribers should handle an overflow of data received from this
 * gracefully, for example using one of the strategies from {@link FlowStrategyType}.
 *
 * @param <Topic> the type of the topics that can be subscribed to
 * @param <Value> the type of values flowing through this multicast
 */
public interface FlowableMulticast<Topic extends Object, Value extends Object>
        extends FlowableProcessor<Value, Value> {

    /**
     * Subscribe to a {@link Topic} by retrieving a Publisher that publishes values
     * which were multicast under the given Topic
     * @param topic the {@link Topic} to subscribe to
     * @return An instance of publisher that streams values multicast under the given topic
     */
    Publisher<Value> subscribeTo(Topic topic);

    /**
     * Like {@code subscribeTo(Topic)}, but immediately subscribes the passed
     * {@link Subscriber} to a publisher that streams values which were multicast
     * under the given {@link Topic}
     * @param topic the {@link Topic} to subscribe to
     * @param subscriber the {@link Subscriber} which should subscribe a {@link Publisher}
     *                   publishing values under that topic
     */
    default void subscribe(Topic topic, Subscriber<Value> subscriber) {
        subscribeTo(topic).subscribe(subscriber);
    }

    /**
     * Returns a publisher that publishes all values multicast,
     * not bound by a specific topic.
     *
     * @return A {@link Publisher} streaming all incoming values
     */
    default Publisher<Value> subscribeToAll() {
        return asPublisher();
    }

    /**
     * Multicast an instance of {@link Value} to all Subscribers
     * that have subscribed to the given {@link Topic}
     *
     * @param topic the topic to multicast on
     * @param value the value to multicast
     */
    default void multicast(Topic topic, Value value) {
        asSubscriber().onNext(value);
    }

    /**
     * {@inheritDoc}
     *
     * This is defined to be internal for the multicast,
     * as it is assumed that there will be parallelization
     * of multicasting.
     */
    @Override
    default ClockType getClockType() {
        return ClockType.INTERNAL;
    }
}
