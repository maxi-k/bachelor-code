/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class LatestFlowStrategy<T> implements FlowStrategy<T, T> {

    private LatestFlowStrategy() {}

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
