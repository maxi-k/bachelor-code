/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.Flowable;

/**
 * Utility interface for Objects which require switchable clock mechanics.
 *
 * Provides a FlowStrategy using {@link #getFlowStrategy()} that can be integrated
 * into a {@link Flowable}, and which guarantees to tick on the schedule provided
 * by the {@link TimeProvider} set using {@link #runOnClock(TimeProvider)}.
 *
 * The intention is that an implementation of {@link Clockable} can use this to
 * satisfy the requirements without building the necessary functionality itself,
 * instead being able to refer to an internal instance of an implementation of
 * {@link ClockState}.
 *
 * @param <T> The type received by the resulting {@link FlowStrategy}
 * @param <R> The type sent out by the resulting {@link FlowStrategy},
 *           presumably after somehow combining the longs emitted by a
 *           {@link TimeProvider#getTicks()} and the input elements of
 *           type {@link T}
 */
public interface ClockState<T extends Object, R extends Object> extends Flowable, Clockable {

    @Override
    FlowStrategy<T, R> getFlowStrategy();
}
