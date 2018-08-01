/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

import java.util.function.Function;

public class BufferFlowStrategy<T> implements FlowStrategy<T, T> {

    private final Function<Flux<T>, Flux<T>> transformer;

    private BufferFlowStrategy(Function<Flux<T>, Flux<T>> transformer) {
        this.transformer = transformer;
    }

    public static <T> BufferFlowStrategy<T> create() {
        return new BufferFlowStrategy<>(Flux::onBackpressureBuffer);
    }

    public static <T> BufferFlowStrategy<T> create(int bufSize) {
        return new BufferFlowStrategy<>((f) -> f.onBackpressureBuffer(bufSize));
    }

    public static <T> BufferFlowStrategy<T> create(int bufSize, BufferOverflowStrategy overflowStrategy) {
        return new BufferFlowStrategy<>((f) -> f.onBackpressureBuffer(bufSize, overflowStrategy));
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.BUFFER;
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
            return Flux
                    .from(publisher)
                    .as(transformer);
    }
}
