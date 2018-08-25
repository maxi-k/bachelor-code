/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.IgnoringFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TransparentFlowStrategy;

public class IgnoringClockState<T extends Object> implements ClockState<T, T> {

    private final FlowStrategy<T, T> strategy;
    private final ClockType clockType;

    private IgnoringClockState(ClockType clockType) {
        this.clockType = clockType;
        this.strategy = new TransparentFlowStrategy<>();
    }

    @Override
    public FlowStrategy<T, T> getFlowStrategy() {
        return strategy;
    }

    @Override
    public boolean setTimer(TimeProvider provider) {
        return false;
    }

    @Override
    public ClockType getClockType() {
        return clockType;
    }
}
