/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.BiFunction;
/**
 * Implementation of {@link FlowStrategy} used for folding a function over the
 * transformed publisher. The folding-function is applied to the newly received
 * value and the value before that to produce the passed on value:
 * {@code (prevValue, newValue) -> foldedValue}
 *
 * @param <T> the type of object received and published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class FolderFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    /**
     * The function used for folding over the values.
     */
    private final BiFunction<T, T, T> folder;

    /**
     * Create a new instance of {@link FolderFlowStrategy} using the
     * given folder function
     * @param folder the function to apply to the elements traversing
     *               the transformed publisher
     */
    private FolderFlowStrategy(BiFunction<T, T, T> folder) {
        this.folder = folder;
    }

    /**
     * Create a new instance of {@link FolderFlowStrategy} using the
     * given folder function
     * @param folder the function to apply to the elements traversing
     *               the transformed publisher
     * @param <T> the type of object received and published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link FolderFlowStrategy}
     */
    public static <T> FolderFlowStrategy<T> create(BiFunction<T, T, T> folder) {
        return new FolderFlowStrategy<>(folder);
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.REDUCE;
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux
                .from(publisher)
                .scan(folder)
                .onBackpressureLatest()
                .tag(PROPERTY_NAME, getType().name());
    }
}
