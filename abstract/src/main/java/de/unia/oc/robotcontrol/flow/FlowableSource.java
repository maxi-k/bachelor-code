/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import ba.playground.reactor.flow.strategy.FlowStrategy;
import org.reactivestreams.Publisher;

public interface FlowableSource<Output> extends Flowable {

    Publisher<Output> getPublisher();

    @Override
    FlowStrategy<Output, Output> getFlowStrategy();

    default Publisher<Output> asPublisher() {
        return getFlowStrategy().apply(getPublisher());
    }

}
