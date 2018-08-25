/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.checkerframework.dataflow.qual.Pure;

import java.util.function.Function;

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

    class ClockType {

        public boolean runOn(TimeProvider timeProvider) { return false; }

        /**
         * The execution is not clocked. It happens at unknown times due
         * to internal or external effects, but not on a schedule.
         */
        public static final ClockType UNCLOCKED = new ClockType();

        /**
         * The execution schedule is set and ensured internally, by the object
         * itself.
         */
        public static final ClockType INTERNAL = new ClockType();

        /**
         * A (possibly changing) execution schedule is provided by the system.
         * The clocked Object needs to accept the provided Clock and adjust
         * its behavior accordingly.
         */
        public static final ClockType createClocked(Function<TimeProvider, Boolean> acceptor) {
            return new ClockedClockType(acceptor);
        }

        private static final class ClockedClockType extends ClockType {

            private final Function<TimeProvider, Boolean> acceptor;

            private ClockedClockType(Function<TimeProvider, Boolean> acceptor) {
                this.acceptor = acceptor;
            }

            @Override
            public boolean runOn(TimeProvider timeProvider) {
                return acceptor.apply(timeProvider);
            }
        }
    }
}
