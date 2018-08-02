/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;

public class TransparentFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    public TransparentFlowStrategy() {}

    public static <T> TransparentFlowStrategy<T> create() {
        return new TransparentFlowStrategy<>();
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.UNDEFINED;
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return publisher;
    }
}
