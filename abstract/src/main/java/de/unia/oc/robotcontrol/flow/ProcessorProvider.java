/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public interface ProcessorProvider<Input, Output> extends FlowProvider<Input, Output> {

    Processor<Input, Output> asProcessor();

    @Override
    default Publisher<Output> asPublisher() {
        return asProcessor();
    }

    @Override
    default Subscriber<Input> asSubscriber() {
        return asProcessor();
    }

}
