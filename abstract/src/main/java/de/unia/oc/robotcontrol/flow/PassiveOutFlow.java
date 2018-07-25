package de.unia.oc.robotcontrol.flow;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Describes a passive {@link OutFlow} that is also not
 * scheduled, as it only provides items.
 * @param <T> The type of item this provides
 */
public interface PassiveOutFlow<T> extends OutFlow<T> {

    @Override
    default PressureType getFlowPressure() {
        return PressureType.PASSIVE;
    }

    @Override
    default @Nullable Runnable getTask() {
        return null;
    }

}
