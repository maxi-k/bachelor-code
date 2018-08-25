/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import javax.swing.text.FlowView;
import java.util.function.Function;

public class MappingFlowStrategy<T extends Object, R extends Object> implements FlowStrategy<T, R> {

    private final Function<? super T, ? extends R> transformer;

    private MappingFlowStrategy(Function<? super T, ? extends R> transformer) {
        this.transformer = transformer;
    }

    public static <T extends Object, R extends Object> MappingFlowStrategy<T, R> create(Function<? super T, ? extends R> transformer) {
        return new MappingFlowStrategy<>(transformer);
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.TRANSPARENT;
    }

    @Override
    public Publisher<R> apply(Publisher<T> publisher) {
        return Flux
                .from(publisher)
                .map(transformer);
    }
}
