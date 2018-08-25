/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TimedFlowStrategy<Input extends Object, Output extends Object> implements FlowStrategy<Input, Output> {

    private final Publisher<Long> timer;
    private final Supplier<Input> initialInput;
    private final BiFunction<Long, Input, Output> mergingFunction;

    private TimedFlowStrategy(Publisher<Long> timer,
                              Supplier<Input> initialInput,
                              BiFunction<Long, Input, Output> mergingFunction) {
        this.timer = timer;
        this.initialInput = initialInput;
        this.mergingFunction = mergingFunction;
    }

    public static <I, O> TimedFlowStrategy<I, O> createTimed(Publisher<Long> timer,
                                                             Supplier<I> initialInput,
                                                             BiFunction<Long, I, O> mergingFunction) {
        return new TimedFlowStrategy<>(timer, initialInput, mergingFunction);
    }

    public static <T> TimedFlowStrategy<T, T> createTimed(Publisher<Long> timer, Supplier<T> initialInput) {
        return createTimed(timer, initialInput, (l, t) -> t);
    }

    public static <I, O> TimedFlowStrategy<I, O> createDurational(Duration duration, Supplier<I> initialInput, BiFunction<Long, I, O> mergingFunction) {
        return createTimed(Flux.interval(duration), initialInput, mergingFunction);
    }

    public static <I, O> TimedFlowStrategy<I, O> createDurational(Publisher<Duration> durationSupplier,
                                                                  Supplier<I> initialInput,
                                                                  BiFunction<Long, I, O> mergingFunction) {
        return createTimed(
                Flux.switchOnNext(Flux.from(durationSupplier).map(Flux::interval)),
                initialInput,
                mergingFunction);
    }

    public static <T> TimedFlowStrategy<T, T> createDurational(Publisher<Duration> durationSupplier, Supplier<T> initialInput) {
        return createTimed(Flux.switchOnNext(Flux.from(durationSupplier).map(Flux::interval)), initialInput);
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.TRANSPARENT;
    }

    @Override
    public Publisher<Output> apply(Publisher<Input> publisher) {
        return Flux.combineLatest(
                timer,
                Flux.from(publisher).startWith(initialInput.get()),
                mergingFunction)
                .doOnNext(System.out::println);
    }
}
