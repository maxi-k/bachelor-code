/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.IgnoringFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TransparentFlowStrategy;

/**
 * An implementation of {@link ClockState} which does nothing when
 * {@link #runOnClock(TimeProvider)} is called (returns false),
 * for use with {@link Clockable} Elements which are not clocked.
 * @param <T> the type used by the {@link FlowStrategy}
 */
public class IgnoringClockState<T extends Object> implements ClockState<T, T> {

    /**
     * The flow strategy returned by {@link #getFlowStrategy()}.
     * Because this {@link ClockState} does not accept {@link TimeProvider}s,
     * this is a dud.
     */
    private final FlowStrategy<T, T> strategy;
    /**
     * The {@link ClockType} that should be returned by {@link #getClockType()}
     */
    private final ClockType clockType;

    /**
     * Create a new {@link IgnoringClockState} with the given {@link ClockType}
     * @param clockType the clock type to return on {@link #getClockType()}
     */
    private IgnoringClockState(ClockType clockType) {
        this.clockType = clockType;
        this.strategy = new TransparentFlowStrategy<>();
    }

    /**
     * Create a new {@link IgnoringClockState} with the given {@link ClockType}
     * Mirror of {@link IgnoringClockState#IgnoringClockState(ClockType)}
     *
     * @param clockType the clock type to return on {@link #getClockType()}
     * @param <T> the type of the clock state used in {@link #getFlowStrategy()}
     * @return a new instance of {@link IgnoringClockState}
     */
    public static <T extends Object> IgnoringClockState<T> create(ClockType clockType) {
        return new IgnoringClockState<>(clockType);
    }

    @Override
    public FlowStrategy<T, T> getFlowStrategy() {
        return strategy;
    }

    /**
     * {@inheritDoc}
     *
     * Does not do anything, always returns false.
     *
     * @param provider the provider on which to schedule the execution
     * @return false
     */
    @Override
    public boolean runOnClock(TimeProvider provider) {
        return false;
    }

    @Override
    public ClockType getClockType() {
        return clockType;
    }
}
