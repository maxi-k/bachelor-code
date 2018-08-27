/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.flow.function.PublisherTransformation;
import org.reactivestreams.Publisher;

/**
 * A {@link FlowableTransformer} is an intermediary step in a data pipeline, classically
 * called a 'Filter' in the 'Pipes & Filters' architecture.
 *
 * For complex transformations that alter the structure of the pipline itself, for example
 * by branching it, or transformations that perform very expensive tasks,
 * it is best to use a {@link FlowableProcessor} instead.
 *
 * @param <Input> the type of data this accepts as input
 * @param <Output> the type of data this outputs
 */
public interface FlowableTransformer<Input extends Object, Output extends Object>
        extends Flowable, PublisherTransformation<Input, Output> {

    @Override
    FlowStrategy<Input, Output> getFlowStrategy();

    @Override
    default Publisher<Output> apply(Publisher<Input> publisher) {
        return getFlowStrategy().apply(publisher);
    }
}
