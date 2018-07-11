package de.unia.oc.robotcontrol.concurrent;

/**
 * An interface that describes something which can be scheduled
 */
public interface Schedulable {

    /**
     * Describes whether this is should be scheduled or not
     * @return
     */
    boolean isScheduled();

    /**
     *
     * @return A {@link Runnable} instance which is to be scheduled
     */
    Runnable getTask();
}
