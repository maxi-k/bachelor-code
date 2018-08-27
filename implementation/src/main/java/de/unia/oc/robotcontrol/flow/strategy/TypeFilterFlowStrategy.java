/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class TypeFilterFlowStrategy<Input extends Object, Output extends Object>
        implements FlowStrategy<Input, Output> {

    private final Class<Output> outputCls;

    private TypeFilterFlowStrategy(Class<Output> outputCls) {
        this.outputCls = outputCls;
    }

    public static <Input, Output> TypeFilterFlowStrategy<Input, Output> create(Class<Output> outputCls) {
        return new TypeFilterFlowStrategy<>(outputCls);
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.TRANSPARENT;
    }

    @Override
    public Publisher<Output> apply(Publisher<Input> inputPublisher) {
        final Object unique = new Object();
        return Flux.from(inputPublisher)
                .map((i) -> outputCls.isInstance(i) ? i : unique)
                .filter((i) -> i != unique)
                .cast(outputCls);
    }
}
