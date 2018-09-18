/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * Implementation of {@link FlowStrategy} used for holding the last
 * received object while a recipient of the transformed Publisher is not ready,
 * then passing it once it is ready.
 *
 * @param <T> the type of object received and published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class LatestFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    private LatestFlowStrategy() {}

    /**
     * Create a new instance of {@link LatestFlowStrategy}.
     * @param <T> the type of object received and published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link LatestFlowStrategy}
     */
    public static <T> LatestFlowStrategy<T> create() {
        return new LatestFlowStrategy<>();
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.LATEST;
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux
                .from(publisher)
                .onBackpressureLatest()
                .tag(PROPERTY_NAME, getType().name());
    }
}
