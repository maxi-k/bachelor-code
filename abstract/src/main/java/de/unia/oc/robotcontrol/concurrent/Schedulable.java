package de.unia.oc.robotcontrol.concurrent;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

/**
 * An interface that describes something which can be scheduled
 */
public interface Schedulable {

    /**
     * Describes whether this is should be scheduled or not.
     *
     * @return
     */
    // @EnsuresNonNullIf(expression = "getTask()", result = true)
    default boolean isScheduled() {
        return getTask() != null;
    }

    /**
     *
     * @return A {@link Runnable} instance which is to be scheduled
     */
    @Pure
    @Nullable
    Runnable getTask();
}
