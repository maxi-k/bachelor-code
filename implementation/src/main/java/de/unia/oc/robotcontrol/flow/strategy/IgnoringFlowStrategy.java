/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * Implementation of {@link FlowStrategy} used for ignoring values
 * that cannot be processed by a recipient. The values are not held,
 * but simply dropped while the recipient is not ready
 *
 * @param <T> the type of object received and published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class IgnoringFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    private IgnoringFlowStrategy() {}

    /**
     * Create a new instance of {@link IgnoringFlowStrategy}.
     * @param <T> the type of object received and published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link IgnoringFlowStrategy}
     */
    public static <T> IgnoringFlowStrategy<T> create() {
        return new IgnoringFlowStrategy<>();
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.IGNORE;
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux
                .from(publisher)
                .onBackpressureDrop()
                .tag(PROPERTY_NAME, getType().name());
    }
}
