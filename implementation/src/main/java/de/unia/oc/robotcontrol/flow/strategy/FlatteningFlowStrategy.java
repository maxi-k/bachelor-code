/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * Implementation of {@link FlowStrategy} used for flattening the values from
 * a publisher of publishers into a publisher of {@link T}, where {@link T} is
 * the type of value wrapped by the publishers:
 * {@code (Publisher<Publisher<T>>) -> (Publisher<T>)}
 *
 * @param <T> the type of object received and published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class FlatteningFlowStrategy<T extends Object> implements FlowStrategy<Publisher<T>, T> {

    private FlatteningFlowStrategy() {}

    /**
     * Create a new instance of {@link FlatteningFlowStrategy}.
     * @param <T> the type of values wrapped by the published publishers,
     *           and that will be published by the transformed publisher
     * @return A new instance of {@link FlatteningFlowStrategy}
     */
    public static <T extends Object> FlatteningFlowStrategy<T> create() {
        return new FlatteningFlowStrategy<>();
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.TRANSPARENT;
    }

    @Override
    public Publisher<T> apply(Publisher<Publisher<T>> publisher) {
        return Flux.switchOnNext(publisher);
    }
}
