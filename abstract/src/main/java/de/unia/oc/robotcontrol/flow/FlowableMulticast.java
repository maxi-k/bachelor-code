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

    Publisher<Value> subscribeTo(Topic topic);

    default void subscribe(Topic topic, Subscriber<Value> subscriber) {
        subscribeTo(topic).subscribe(subscriber);
    }

    default Publisher<Value> subscribeToAll() {
        return asPublisher();
    }

    default void multicast(Topic topic, Value value) {
        asSubscriber().onNext(value);
    }

    @Override
    default ClockType getClockType() {
        return ClockType.INTERNAL;
    }
}
