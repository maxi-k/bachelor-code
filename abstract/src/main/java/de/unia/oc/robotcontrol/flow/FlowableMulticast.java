/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

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
