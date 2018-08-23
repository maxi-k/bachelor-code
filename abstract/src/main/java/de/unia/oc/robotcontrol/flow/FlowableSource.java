/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.Clockable;
import org.reactivestreams.Publisher;

public interface FlowableSource<Output extends Object> extends Flowable, Clockable {

    Publisher<Output> asPublisher();

}
