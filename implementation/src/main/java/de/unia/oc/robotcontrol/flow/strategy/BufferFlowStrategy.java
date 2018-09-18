/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

import java.util.function.Function;

/**
 * Implementation of {@link FlowStrategy} used for buffering values
 * of a publisher. Uses {@link Flux#onBackpressureBuffer()} internally.
 *
 * @param <T> the type of object received and published by the Publisher
 *           transformed by this flow strategy
 */
public class BufferFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    /**
     * The transformer which is applied to the Publisher.
     */
    private final Function<Flux<T>, Flux<T>> transformer;

    /**
     * Creates a new {@link BufferFlowStrategy} which transforms the
     * given flux using the passed function.
     *
     * @param transformer the transforming function
     */
    private BufferFlowStrategy(Function<Flux<T>, Flux<T>> transformer) {
        this.transformer = transformer;
    }

    /**
     * Creates a new {@link BufferFlowStrategy} with an unbounded buffer.
     * @param <T> the type of object received and published by the Publisher
     *           transformed by this flow strategy
     * @return a new instance of {@link BufferFlowStrategy}
     */
    public static <T> BufferFlowStrategy<T> create() {
        return new BufferFlowStrategy<>(Flux::onBackpressureBuffer);
    }

    /**
     * Creates a new {@link BufferFlowStrategy} with a bounded buffer.
     * @param bufSize the buffer size to use
     * @param <T> the type of object received and published by the Publisher
     *           transformed by this flow strategy
     * @return a new instance of {@link BufferFlowStrategy}
     */
    public static <T> BufferFlowStrategy<T> create(int bufSize) {
        return new BufferFlowStrategy<>((f) -> f.onBackpressureBuffer(bufSize));
    }

    /**
     * Creates a new {@link BufferFlowStrategy} with a bounded buffer. Uses
     * the given overflow strategy to determine what happens when the buffer
     * overflows.
     * @param bufSize the buffer size to use
     * @param overflowStrategy the overflow strategy to use to determine what
     *                         should happen when the buffer overflows
     * @param <T> the type of object received and published by the Publisher
     *           transformed by this flow strategy
     * @return a new instance of {@link BufferFlowStrategy}
     */
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
