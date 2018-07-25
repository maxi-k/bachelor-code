package de.unia.oc.robotcontrol.flow;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Describes a passive {@link InFlow} that is also not
 * scheduled, as it only accepts items.
 * @param <T> The type of item this accepts
 */
public interface PassiveInFlow<T> extends InFlow<T> {

    @Override
    default PressureType getFlowPressure() {
        return PressureType.PASSIVE;
    }

    @Override
    default @Nullable Runnable getTask() {
        return null;
    }

}
