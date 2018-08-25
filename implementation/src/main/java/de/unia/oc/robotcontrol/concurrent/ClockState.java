/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import de.unia.oc.robotcontrol.flow.Flow;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.FlatteningFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.MappingFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TimedFlowStrategy;
import org.reactivestreams.Processor;

public class ClockState<T extends Object> implements Clockable {

    private final Processor<TimeProvider, Long> timerPublisher ;
    private final FlowStrategy<T, T> strategy;
    private final ClockType clockType;

    @SuppressWarnings("initialization")
    private ClockState() {
        this.timerPublisher = Flow.withProcessor(
                FlowStrategy.concat(
                        MappingFlowStrategy.create(TimeProvider::getTicks),
                        FlatteningFlowStrategy.create()
                )
        );
        this.strategy = TimedFlowStrategy.createTimed(timerPublisher);

        this.clockType = ClockType.createClocked(this::setTimer);
    }

    public static <T extends Object> ClockState<T> create() {
        return new ClockState<>();
    }

    public FlowStrategy<T, T> getFlowStrategy() {
        return strategy;
    }

    public boolean setTimer(TimeProvider provider) {
        timerPublisher.onNext(provider);
        return true;
    }

    @Override
    public ClockType getClockType() {
        return clockType;
    }

    @Override
    public ConcurrencyType getConcurrencyType() {
        return ConcurrencyType.EXTERNAL;
    }
}
