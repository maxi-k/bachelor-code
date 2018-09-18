/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;


import de.unia.oc.robotcontrol.flow.function.ProcessorTransformation;
import de.unia.oc.robotcontrol.flow.strategy.*;
import de.unia.oc.robotcontrol.util.Tuple;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.EmitterProcessor;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Class with utility methods for the flow module of
 * the project ({@link de.unia.oc.robotcontrol.flow})
 * ("module-class").
 *
 * This only contains static methods and cannot be instantiated.
 */
public final class Flow {

    private Flow() {}

    public static <T extends Object, R extends Object> Publisher<R> applyStrategy(Publisher<T> publisher, FlowStrategy<T, R> strategyProvider) {
        return strategyProvider.apply(publisher);
    }

    public static <T extends Object> Publisher<T> applyIgnoringStrategy(Publisher<T> publisher) {
        return applyStrategy(publisher, IgnoringFlowStrategy.create());
    }

    public static <T extends Object> Publisher<T> applyErrorStrategy(Publisher<T> publisher) {
        return applyStrategy(publisher, ErrorFlowStrategy.create());
    }

    public static <T extends Object> Publisher<T> applyLatestStrategy(Publisher<T> publisher) {
        return applyStrategy(publisher, LatestFlowStrategy.create());
    }

    public static <T extends Object> Publisher<T> applyBufferStrategy(Publisher<T> publisher) {
        return applyStrategy(publisher, BufferFlowStrategy.create());
    }

    public static <T extends Object> Publisher<T> applyBufferStrategy(Publisher<T> publisher, int bufferSize) {
        return applyStrategy(publisher, BufferFlowStrategy.create(bufferSize));
    }

    public static <T extends Object, R extends Object> Publisher<R> applyReducerStrategy(Publisher<T> publisher, Supplier<R> initialValueSupplier, BiFunction<R, T, R> reducer) {
        return applyStrategy(publisher, ReducerFlowStrategy.create(initialValueSupplier, reducer));
    }

    public static <T extends Object> Publisher<T> applyReducerStrategy(Publisher<T> publisher, BiFunction<T, T, T> folder) {
        return applyStrategy(publisher, FolderFlowStrategy.create(folder));
    }
    public static <T extends Object, R extends Object> Processor<T, R> withProcessor(FlowStrategy<T, R> strategy) {
        EmitterProcessor<T> processor = EmitterProcessor.create();
        return ProcessorTransformation.transformProcessor(processor, Function.identity(), strategy);
    }

    public static <S extends Object, T extends Object, R extends Object> Processor<S, R> withProcessor(
            Processor<S, T> processor,
            FlowStrategy<T, R> strategy
    ) {
        return ProcessorTransformation.transformProcessor(processor, Function.identity(), strategy);
    }

    public static <T extends Object> Publisher<T> fromSetter(Consumer<Consumer<T>> setter) {
        DirectProcessor<T> processor = DirectProcessor.create();
        setter.accept(processor::onNext);
        return processor;
    }

    public static <T extends Object> Publisher<T> fromCallback(Consumer<Subscriber<T>> setter) {
        DirectProcessor<T> processor = DirectProcessor.create();
        setter.accept(processor);
        return processor;
    }
}
