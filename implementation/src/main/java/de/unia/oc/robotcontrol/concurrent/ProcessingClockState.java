/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import de.unia.oc.robotcontrol.flow.Flow;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.FlatteningFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.MappingFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TimedFlowStrategy;
import org.reactivestreams.Processor;
import reactor.core.publisher.EmitterProcessor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ProcessingClockState<T extends Object, R extends Object>
        implements ClockState<T, R> {

    private final Processor<TimeProvider, Long> timerPublisher ;
    private final FlowStrategy<T, R> strategy;
    private final Clockable.ClockType clockType;

    @SuppressWarnings("initialization")
    private ProcessingClockState(Supplier<T> initialInput, BiFunction<Long, T, R> mergingFunction) {
        this.timerPublisher = Flow.withProcessor(
                EmitterProcessor.create(),
                FlowStrategy.concat(
                        MappingFlowStrategy.create(TimeProvider::getTicks),
                        FlatteningFlowStrategy.create()
                )
        );

        this.strategy = TimedFlowStrategy.createTimed(timerPublisher, initialInput, mergingFunction);
        this.clockType = Clockable.ClockType.createClocked(this::setTimer);
    }

    public static <T extends Object, R extends Object> ProcessingClockState<T, R> create(
            Supplier<T> initialInput,
            BiFunction<Long, T, R> mergingFunction) {
        return new ProcessingClockState<>(initialInput, mergingFunction);
    }

    public static <T extends Object> ProcessingClockState<T, T> createReplaying(Supplier<T> initialInput) {
        return new ProcessingClockState<>(initialInput, (l, t) -> t);
    }

    public FlowStrategy<T, R> getFlowStrategy() {
        return strategy;
    }

    public boolean setTimer(TimeProvider provider) {
        timerPublisher.onNext(provider);
        return true;
    }

    @Override
    public ConcurrencyType getConcurrencyType() {
        return ConcurrencyType.EXTERNAL;
    }

    @Override
    public ClockType getClockType() {
        return clockType;
    }
}
