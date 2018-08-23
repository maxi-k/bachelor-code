/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.checkerframework.dataflow.qual.Pure;

/**
 * Describes something whose execution may be clocked,
 * that is, executed multiple times on a fixed or dynamic
 * schedule.
 */
public interface Clockable extends Concurrent {

    /**
     * How is the execution scheduled?
     * Does the Object set the clock itself,
     * or should the system provide a schedule?
     *
     * This should remain constant for each object.
     *
     * @return a (constant) {@link ClockType}
     */
    @Pure
    ClockType getClockType();

    enum ClockType {

        /**
         * The execution is not clocked. It happens at unknown times due
         * to internal or external effects, but not on a schedule.
         */
        UNCLOCKED,

        /**
         * The execution schedule is set and ensured internally, by the object
         * itself.
         */
        INTERNAL,

        /**
         * A (possibly changing) execution schedule is provided by the system.
         */
        EXTERNAL;
    }
}
