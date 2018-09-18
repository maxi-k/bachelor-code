/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

/**
 * Implementation of {@link FlowStrategy} used for holding the first value
 * which could not be sent to a recipient because it's busy, and
 * sending it once the recipient is done.
 *
 * @param <T> the type of object received and published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class OldestFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    private OldestFlowStrategy() {}

    /**
     * Create a new instance of {@link OldestFlowStrategy}.
     * @param <T> the type of object received and published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link OldestFlowStrategy}
     */
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
