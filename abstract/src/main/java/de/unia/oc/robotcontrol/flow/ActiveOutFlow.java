package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.Schedulable;

import java.util.function.Consumer;

/**
 * Describes an active {@link OutFlow} that will push items
 * to some {@link InFlow}. This pushing can be scheduled
 * (see {@link Schedulable}) or triggered by something else.
 *
 * @param <T> The type of item this pushes
 */
public interface ActiveOutFlow<T> extends OutFlow<Consumer<InFlow<T>>> {

    @Override
    default PressureType getFlowPressure() {
        return PressureType.ACTIVE;
    }
}
