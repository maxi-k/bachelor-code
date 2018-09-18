/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * Implementation of {@link FlowStrategy} used for emitting errors when
 * a recipient of values from a publisher cannot process them.
 *
 * @param <T> the type of object received and published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class ErrorFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    private ErrorFlowStrategy() {}

    /**
     * Create a new Instance of {@link ErrorFlowStrategy}
     * @param <T> the type of object received and published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link ErrorFlowStrategy}
     */
    public static <T> ErrorFlowStrategy<T> create() {
        return new ErrorFlowStrategy<>();
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.ERROR;
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux
                .from(publisher)
                .onBackpressureError()
                .tag(PROPERTY_NAME, getType().name());
    }
}
