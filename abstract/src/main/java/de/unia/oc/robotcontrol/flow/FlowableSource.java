/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.reactivestreams.Publisher;

public interface FlowableSource<Output extends Object> extends Flowable {

    Publisher<Output> asPublisher();

}
