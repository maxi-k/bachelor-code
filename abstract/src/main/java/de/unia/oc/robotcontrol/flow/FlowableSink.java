/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Subscriber;

public interface FlowableSink<Input extends Object> {

    Subscriber<Input> asSubscriber();

    default boolean castOnNext(Object obj) {
        try {
            @SuppressWarnings("unchecked")
            Input asInput = (Input) obj;
            asSubscriber().onNext(asInput);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
