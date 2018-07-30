/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

public enum ClockType {

    /**
     * The task has an internal clock, that is, it is a continuous {@link Runnable} which
     * presumably executes in a while loop or something similar according to its own time.
     */
    INTERNAL,

    /**
     * The task needs an external clock, that is, it runs and finishes after some time,
     * and needs to be run again by some external scheduler using that schedulers speed.
     */
    EXTERNAL;
}
