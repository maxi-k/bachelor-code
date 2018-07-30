/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Subscriber;

public interface SubscriberProvider<T> {

    Subscriber<T> asSubscriber();
}
