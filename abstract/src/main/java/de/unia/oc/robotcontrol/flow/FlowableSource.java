/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.Clockable;
import org.reactivestreams.Publisher;

/**
 * A {@link FlowableSource} is a source for data flowing throughout the program,
 * that is, the beginning of a pipeline. Corresponds to {@link Publisher}.
 *
 * For Elements that publish data, but received them from another pipeline, use
 * {@link FlowableTransformer} instead.
 *
 * @param <Output> the type of data this outputs
 */
public interface FlowableSource<Output extends Object> extends Flowable, Clockable {

    /**
     * Get a view on this {@link FlowableSource} which adheres
     * to the reactive-streams {@link Publisher} specification
     *
     * @return an instance of {@link Publisher}
     */
    Publisher<Output> asPublisher();

}
