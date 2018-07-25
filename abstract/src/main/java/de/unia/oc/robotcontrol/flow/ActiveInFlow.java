package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.Schedulable;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Supplier;

/**
 * Describes an active {@link InFlow} that will pull items
 * from some {@link OutFlow}. This pulling can be scheduled
 * (see {@link Schedulable}) or triggered by something else.
 *
 * @param <T> The type of item this pulls
 */
public interface ActiveInFlow<T> extends InFlow<Supplier<OutFlow<T>>> {

    @Override
    default PressureType getFlowPressure() {
        return PressureType.ACTIVE;
    }

    @Override
    @Nullable Runnable getTask();
}
