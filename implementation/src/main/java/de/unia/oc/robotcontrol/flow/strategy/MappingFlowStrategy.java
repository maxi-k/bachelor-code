/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import javax.swing.text.FlowView;
import java.util.function.Function;

/**
 * Implementation of {@link FlowStrategy} used for mapping values one-by-one
 * using a mapping function as defined by {@link #transformer}.
 *
 * @param <T> the type of object published by the Publisher
 *           which is to be transformed by this flow strategy
 * @param <R> the type of object published by the transformed Publisher
 */
public class MappingFlowStrategy<T extends Object, R extends Object> implements FlowStrategy<T, R> {

    /**
     * The function used to transform values.
     */
    private final Function<? super T, ? extends R> transformer;

    /**
     * Create a new instance of {@link MappingFlowStrategy} using
     * the given transformer to map values.
     * @param transformer the transforming function used to map values
     */
    private MappingFlowStrategy(Function<? super T, ? extends R> transformer) {
        this.transformer = transformer;
    }

    /**
     *
     * Create a new instance of {@link MappingFlowStrategy} using
     * the given transformer to map values.
     * @param transformer the transforming function used to map values
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @param <R> the type of object published by the transformed Publisher
     * @return a new instance of {@link MappingFlowStrategy}
     */
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
