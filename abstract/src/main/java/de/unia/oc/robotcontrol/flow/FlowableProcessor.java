/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public interface FlowableProcessor<Input extends Object, Output extends Object>
        extends FlowableSink<Input>, FlowableSource<Output>, FlowableTransformer<Input, Output> {

    Processor<Input, Output> asProcessor();

    @Override
    default Subscriber<Input> asSubscriber() {
        return asProcessor();
    }

    @Override
    default Publisher<Output> asPublisher() {
        return asProcessor();
    }

    @Override
    default Publisher<Output> apply(Publisher<Input> publisher) {
        Processor<Input, Output> p = asProcessor();
        publisher.subscribe(p);
        return p;
    }

}
