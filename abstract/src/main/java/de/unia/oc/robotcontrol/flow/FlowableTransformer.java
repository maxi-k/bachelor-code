/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Publisher;

import java.util.function.Function;

public interface FlowableTransformer<Input, Output>
        extends Flowable, Function<Publisher<Input>, Publisher<Output>> {

    @Override
    FlowStrategy<Input, Output> getFlowStrategy();

    @Override
    default Publisher<Output> apply(Publisher<Input> publisher) {
        return getFlowStrategy().apply(publisher);
    }
}
