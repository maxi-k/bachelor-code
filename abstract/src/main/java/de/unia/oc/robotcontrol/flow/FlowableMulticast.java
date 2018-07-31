/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.ClockType;
import org.reactivestreams.Publisher;

public interface FlowableMulticast<Topic, Value> extends FlowableProcessor<Value, Value> {

    Publisher<Value> subscribeTo(Topic topic);

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
