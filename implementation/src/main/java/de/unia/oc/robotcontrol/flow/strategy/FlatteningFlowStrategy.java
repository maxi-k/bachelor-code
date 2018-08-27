/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class FlatteningFlowStrategy<T extends Object> implements FlowStrategy<Publisher<T>, T> {

    private FlatteningFlowStrategy() {}

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
