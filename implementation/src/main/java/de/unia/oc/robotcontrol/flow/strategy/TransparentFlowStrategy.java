/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;

import java.util.function.Function;

/**
 * Implementation of {@link FlowStrategy} used for passing values
 * transparently, without changing them or anything. This is
 * akin to the identity function ({@link Function#identity()},
 * and serves as an 'empty' or 'null' value in the space of
 * flow strategies.
 *
 * @param <T> the type of object published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class TransparentFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    public TransparentFlowStrategy() {}

    /**
     * Creates a new {@link TransparentFlowStrategy} instance.
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link TransparentFlowStrategy}
     */
    public static <T> TransparentFlowStrategy<T> create() {
        return new TransparentFlowStrategy<>();
    }

    @Override
    public FlowStrategyType getType(){
        return FlowStrategyType.TRANSPARENT;
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return publisher;
    }
}
