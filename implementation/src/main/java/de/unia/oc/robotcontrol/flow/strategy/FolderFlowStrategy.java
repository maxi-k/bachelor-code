/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.BiFunction;

public class FolderFlowStrategy<T> implements FlowStrategy<T, T> {

    private final BiFunction<T, T, T> folder;

    private FolderFlowStrategy(BiFunction<T, T, T> folder) {
        this.folder = folder;
    }

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
