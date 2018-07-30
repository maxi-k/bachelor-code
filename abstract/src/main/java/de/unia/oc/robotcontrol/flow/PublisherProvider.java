/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Publisher;

public interface PublisherProvider<T> {

    Publisher<T> asPublisher();
}
