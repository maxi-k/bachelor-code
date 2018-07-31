/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Publisher;

public interface FlowableSource<Output> extends Flowable {

    Publisher<Output> asPublisher();

}
