/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class CastMapFlowStrategy<Input extends Object, Output extends Object>
        implements FlowStrategy<Input, Output> {

    private final Object unique = new Object();
    private final Class<Output> outputCls;

    private CastMapFlowStrategy(Class<Output> outputCls) {
        this.outputCls = outputCls;
    }

    public static <Input, Output> CastMapFlowStrategy<Input, Output> create(Class<Output> outputCls) {
        return new CastMapFlowStrategy<>(outputCls);
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.UNDEFINED;
    }

    @Override
    public Publisher<Output> apply(Publisher<Input> inputPublisher) {
        return Flux.from(inputPublisher)
                .map((i) -> outputCls.isInstance(i) ? i : unique)
                .filter((i) -> i == unique)
                .map(outputCls::cast);
    }
}
