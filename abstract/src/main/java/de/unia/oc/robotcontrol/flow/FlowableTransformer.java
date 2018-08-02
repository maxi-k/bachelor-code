/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Publisher;

public interface FlowableTransformer<Input extends Object, Output extends Object>
        extends Flowable, PublisherTransformer<Input, Output> {

    @Override
    FlowStrategy<Input, Output> getFlowStrategy();

    @Override
    default Publisher<Output> apply(Publisher<Input> publisher) {
        return getFlowStrategy().apply(publisher);
    }
}
