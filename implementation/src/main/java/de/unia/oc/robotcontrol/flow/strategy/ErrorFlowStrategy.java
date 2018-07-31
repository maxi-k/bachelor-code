/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class ErrorFlowStrategy<T> implements FlowStrategy<T, T> {

    private ErrorFlowStrategy() {}

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
