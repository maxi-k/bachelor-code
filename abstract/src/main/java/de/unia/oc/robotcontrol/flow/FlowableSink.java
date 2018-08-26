/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Subscriber;

/**
 * A {@link FlowableSink} is a sink for data flowing throughout the program,
 * that is, the endpoint of a pipeline. Corresponds to {@link Subscriber<Input>}.
 *
 * For Elements that receive data but pass them down the pipeline, use
 * {@link FlowableTransformer} instead.
 *
 * @param <Input> the type of data this accepts as input
 */
public interface FlowableSink<Input extends Object> {

    /**
     * Get a view on this {@link FlowableSink} which adheres
     * to the reactive-streams {@link Subscriber} specification
     *
     * @return an instance of {@link Subscriber<Input>}
     */
    Subscriber<Input> asSubscriber();

    /**
     * Try to apply the {@link Subscriber#onNext(Object)} function
     * from {@link #asSubscriber()} to the given generic Object,
     * casting it to {@link Input}.
     *
     * @param obj the object to try to pass to {@link Subscriber#onNext(Object)}
     * @return  {@link true} if the object could be cast to {@link Input} and was passed
     *          {@link false} if the object could was not passed
     */
    default boolean castOnNext(Object obj) {
        try {
            @SuppressWarnings("unchecked")
            Input asInput = (Input) obj;
            asSubscriber().onNext(asInput);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
