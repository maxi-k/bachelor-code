/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class BufferFlowStrategy<T> implements FlowStrategy<T, T> {

    private final int bufferSize;

    private BufferFlowStrategy(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public static <T> BufferFlowStrategy<T> create() {
        return new BufferFlowStrategy<>(-1);
    }

    public static <T> BufferFlowStrategy<T> create(int bufSize) {
        return new BufferFlowStrategy<>(bufSize);
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.BUFFER;
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        if (this.bufferSize < 0) {
            return Flux
                    .from(publisher)
                    .onBackpressureBuffer();
        } else {
            return Flux
                    .from(publisher)
                    .onBackpressureBuffer(bufferSize)
                    .tag(PROPERTY_NAME, getType().name());
        }
    }
}
