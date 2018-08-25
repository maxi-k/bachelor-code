/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import de.unia.oc.robotcontrol.flow.Flow;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.*;
import org.reactivestreams.Processor;

import java.util.function.BiFunction;

public class ProcessingClockState<T extends Object, R extends Object>
        implements ClockState<T, R> {

    private final Processor<TimeProvider, Long> timerPublisher ;
    private final FlowStrategy<T, R> strategy;
    private final Clockable.ClockType clockType;

    @SuppressWarnings("initialization")
    private ProcessingClockState(BiFunction<Long, T, R> mergingFunction) {
        this.timerPublisher = Flow.withProcessor(
                MappingFlowStrategy
                        .create(TimeProvider::getTicks)
                        .with(FlatteningFlowStrategy.create())
                        .with(MappingFlowStrategy.create((t) -> System.currentTimeMillis())
                )
        );

        this.strategy = TimedFlowStrategy.createTimed(timerPublisher, mergingFunction);
        this.clockType = Clockable.ClockType.createClocked(this::setTimer);
    }

    public static <T extends Object, R extends Object> ProcessingClockState<T, R> create(BiFunction<Long, T, R> mergingFunction) {
        return new ProcessingClockState<>(mergingFunction);
    }

    public static <T extends Object> ProcessingClockState<T, T> createReplaying() {
        return new ProcessingClockState<>((l, t) -> t);
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
