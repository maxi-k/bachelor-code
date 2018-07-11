package de.unia.oc.robotcontrol.concurrent;

/**
 * Defines the different modes in which a task that
 * is repeatedly run can operate.
 */
public enum TaskMode {
    /**
     * The task is run at fixed intervals.
     */
    INTERVAL,
    /**
     * The task it run with fixed delays between the
     * end of one execution and the start of another.
     */
    DELAYED,
}
