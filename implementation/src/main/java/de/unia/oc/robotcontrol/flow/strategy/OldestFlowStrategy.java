/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

public class OldestFlowStrategy<T> implements FlowStrategy<T, T> {

    private OldestFlowStrategy() {}

    public static <T> OldestFlowStrategy<T> create() {
        return new OldestFlowStrategy<>();
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.OLDEST;
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux
                .from(publisher)
                .onBackpressureBuffer(1, BufferOverflowStrategy.DROP_LATEST)
                .tag(PROPERTY_NAME, getType().name());
    }
}
