/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * Implementation of {@link FlowStrategy} used for executing a callback
 * whenever a value traverses the transformed Publisher.
 *
 * @param <T> the type of object received and published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class CallbackFlowStrategy<T extends Object> extends TransparentFlowStrategy<T> {

    /**
     * The callback to execute when a value traverses the Publisher
     */
    private Consumer<T> callback;

    /**
     * Create a new {@link CallbackFlowStrategy} using
     * the given callback function.
     *
     * @param callback the function to call when a value traverses
     *                 the transformed publisher
     */
    protected CallbackFlowStrategy(Consumer<T> callback) {
       this.callback = callback;
    }

    /**
     * Create a new {@link CallbackFlowStrategy} instance using the
     * given callback
     * @param callback the function to call when a value traverses
     *                 the transformed publisher
     * @param <T> the type of object received and published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link CallbackFlowStrategy}
     */
    public static <T extends Object> CallbackFlowStrategy<T> create(Consumer<T> callback) {
        return new CallbackFlowStrategy<>(callback);
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux
                .from(super.apply(publisher))
                .doOnNext(callback);
    }
}
