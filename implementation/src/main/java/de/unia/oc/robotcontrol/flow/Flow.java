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

    /**
     * Applies the given {@link FlowStrategy} to the given {@link Publisher},
     * returning a transformed {@link Publisher}.
     *
     * @param publisher the publisher to apply the flow strategy to
     * @param strategyProvider the flow strategy to apply
     * @param <T> the type of the object emitted by the given publisher
     * @param <R> the type of the object emitted by the resulting publisher
     * @return an instance of {@link Publisher}
     */
    public static <T extends Object, R extends Object> Publisher<R> applyStrategy(Publisher<T> publisher, FlowStrategy<T, R> strategyProvider) {
        return strategyProvider.apply(publisher);
    }

    /**
     * Applies a {@link IgnoringFlowStrategy} to the given Publisher.
     *
     * @param publisher the publisher to apply the strategy to
     * @param <T> the type of the values emitted by the given publisher
     * @return an instance of {@link Publisher}
     */
    public static <T extends Object> Publisher<T> applyIgnoringStrategy(Publisher<T> publisher) {
        return applyStrategy(publisher, IgnoringFlowStrategy.create());
    }

    /**
     * Applies a {@link ErrorFlowStrategy} to the given Publisher.
     *
     * @param publisher the publisher to apply the strategy to
     * @param <T> the type of the values emitted by the given publisher
     * @return an instance of {@link Publisher}
     */
    public static <T extends Object> Publisher<T> applyErrorStrategy(Publisher<T> publisher) {
        return applyStrategy(publisher, ErrorFlowStrategy.create());
    }

    /**
     * Applies a {@link LatestFlowStrategy} to the given Publisher.
     *
     * @param publisher the publisher to apply the strategy to
     * @param <T> the type of the values emitted by the given publisher
     * @return an instance of {@link Publisher}
     */
    public static <T extends Object> Publisher<T> applyLatestStrategy(Publisher<T> publisher) {
        return applyStrategy(publisher, LatestFlowStrategy.create());
    }

    /**
     * Applies a {@link BufferFlowStrategy} to the given Publisher.
     *
     * @param publisher the publisher to apply the strategy to
     * @param <T> the type of the values emitted by the given publisher
     * @return an instance of {@link Publisher}
     */
    public static <T extends Object> Publisher<T> applyBufferStrategy(Publisher<T> publisher) {
        return applyStrategy(publisher, BufferFlowStrategy.create());
    }

    /**
     * Applies a {@link BufferFlowStrategy} to the given Publisher, using the
     * given parameter as the maximum buffer size.
     *
     * @param publisher the publisher to apply the strategy to
     * @param bufferSize the maximum buffer size to buffer elements with
     * @param <T> the type of the values emitted by the given publisher
     * @return an instance of {@link Publisher}
     */
    public static <T extends Object> Publisher<T> applyBufferStrategy(Publisher<T> publisher, int bufferSize) {
        return applyStrategy(publisher, BufferFlowStrategy.create(bufferSize));
    }

    /**
     * Applies a {@link ReducerFlowStrategy} to the given Publisher,
     * using the rest of its arguments as parameters.
     *
     * @param publisher the publisher to apply the strategy to
     * @param initialValueSupplier the supplier for the first reduced value
     * @param reducer the reducing function
     * @param <T> the type of the values emitted by the given publisher
     * @param <R> the type of the values emitted by the resulting publisher
     * @return an instance of {@link Publisher}
     */
    public static <T extends Object, R extends Object> Publisher<R> applyReducerStrategy(Publisher<T> publisher, Supplier<R> initialValueSupplier, BiFunction<R, T, R> reducer) {
        return applyStrategy(publisher, ReducerFlowStrategy.create(initialValueSupplier, reducer));
    }

    /**
     * Applies a {@link FolderFlowStrategy} to the given Publisher,
     * using the rest of its arguments as parameters.
     *
     * @param publisher the publisher to apply the strategy to
     * @param folder the folding function
     * @param <T> the type of the values emitted by the given publisher
     * @return an instance of {@link Publisher}
     */
    public static <T extends Object> Publisher<T> applyReducerStrategy(Publisher<T> publisher, BiFunction<T, T, T> folder) {
        return applyStrategy(publisher, FolderFlowStrategy.create(folder));
    }

    /**
     * Creates a new {@link EmitterProcessor} and applies the given strategy to it.
     *
     * @param strategy the strategy to apply
     * @param <T> the type of the values received by the resulting processor
     * @param <R> the type of the values emitted by the resulting processor
     * @return an instance of {@link Processor}
     */
    public static <T extends Object, R extends Object> Processor<T, R> withProcessor(FlowStrategy<T, R> strategy) {
        EmitterProcessor<T> processor = EmitterProcessor.create();
        return ProcessorTransformation.transformProcessor(processor, Function.identity(), strategy);
    }

    /**
     * Transforms the given {@link Processor} using the given {@link FlowStrategy}.
     *
     * @param processor the processor to transform
     * @param strategy the strategy to transform the processor with
     * @param <S> the input type of the given processor
     * @param <T> the output type of the given processor
     * @param <R> the output type of the resulting proessor
     * @return the transformed processor
     */
    public static <S extends Object, T extends Object, R extends Object> Processor<S, R> withProcessor(
            Processor<S, T> processor,
            FlowStrategy<T, R> strategy
    ) {
        return ProcessorTransformation.transformProcessor(processor, Function.identity(), strategy);
    }

    /**
     * Creates a new {@link Publisher} which publishes values given to it through
     * the inner function of the nested consumer, that is, the given consumer is
     * passed the {@link Subscriber#onNext(Object)} function so it can use it
     * to publish values on the returned publisher.
     *
     * @param setter the consumer to pass the consuming function to
     * @param <T> the type of value published
     * @return a new {@link DirectProcessor}
     */
    public static <T extends Object> Publisher<T> fromSetter(Consumer<Consumer<T>> setter) {
        DirectProcessor<T> processor = DirectProcessor.create();
        setter.accept(processor::onNext);
        return processor;
    }

    /**
     * Creates a new {@link Publisher}. The given consumer is passed a
     * {@link Subscriber} which it can use to publish values, pass errors or
     * complete the returned publisher.
     *
     * @param setter the consumer to pass the subscriber to
     * @param <T> the type of value published
     * @return a new {@link DirectProcessor}
     */
    public static <T extends Object> Publisher<T> fromCallback(Consumer<Subscriber<T>> setter) {
        DirectProcessor<T> processor = DirectProcessor.create();
        setter.accept(processor);
        return processor;
    }
}
