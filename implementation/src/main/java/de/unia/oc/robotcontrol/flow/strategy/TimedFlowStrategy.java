/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.BiFunction;

public class TimedFlowStrategy<Input extends Object, Output extends Object> implements FlowStrategy<Input, Output> {

    private final Publisher<Long> timer;
    private final BiFunction<Long, Input, Output> mergingFunction;

    private TimedFlowStrategy(Publisher<Long> timer, BiFunction<Long, Input, Output> mergingFunction) {
        this.timer = timer;
        this.mergingFunction = mergingFunction;
    }


    public static <I, O> TimedFlowStrategy<I, O> createTimed(Publisher<Long> timer,
                                                        BiFunction<Long, I, O> mergingFunction) {
        return new TimedFlowStrategy<>(timer, mergingFunction);
    }

    public static <T> TimedFlowStrategy<T, T> createTimed(Publisher<Long> timer) {
        return createTimed(timer, (l, t) -> t);
    }

    public static <I, O> TimedFlowStrategy<I, O> createDurational(Duration duration, BiFunction<Long, I, O> mergingFunction) {
        return createTimed(Flux.interval(duration), mergingFunction);
    }

    public static <I, O> TimedFlowStrategy<I, O> createDurational(Publisher<Duration> durationSupplier, BiFunction<Long, I, O> mergingFunction) {
        return createTimed(Flux.switchOnNext(Flux.from(durationSupplier).map(Flux::interval)), mergingFunction);
    }

    public static <T> TimedFlowStrategy<T, T> createDurational(Publisher<Duration> durationSupplier) {
        return createTimed(Flux.switchOnNext(Flux.from(durationSupplier).map(Flux::interval)));
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.TRANSPARENT;
    }

    @Override
    public Publisher<Output> apply(Publisher<Input> publisher) {
        return Flux.combineLatest(
                timer,
                publisher,
                mergingFunction);
    }
}
