/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

/**
 * A {@link FlowableProcessor} is both a data sink and data source, and
 * may transform any received data before sending it again.
 *
 * It may break the backpressure established throughout the network,
 * and should only be used for expensive processing or things that cannot be
 * implemented using a {@link FlowableTransformer}.
 *
 * @param <Input> the type of input data this accepts
 * @param <Output> the type of data this outputs to subscribers
 */
public interface FlowableProcessor<Input extends Object, Output extends Object>
        extends
        FlowableSink<Input>,
        FlowableSource<Output>,
        FlowableTransformer<Input, Output> {

    /**
     * Get a view on this {@link FlowableProcessor} which adheres
     * to the reactive-streams {@link Processor} specification
     *
     * @return an instance of {@link Processor<Input, Output>}
     */
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
