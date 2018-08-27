/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class IgnoringFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    private IgnoringFlowStrategy() {}

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
