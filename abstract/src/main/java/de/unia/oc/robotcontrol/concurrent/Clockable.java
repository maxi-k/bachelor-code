/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.checkerframework.dataflow.qual.Pure;

import java.util.function.Function;

/**
 * Describes something whose execution may be clocked,
 * that is, executed multiple times on a fixed or dynamic
 * schedule.
 *
 * It is not specified what 'execution' means in this context.
 * This is assumed to be specified in sub-interfaces or concrete
 * implementations.
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

    /**
     * Schedule the execution of this Object on the given {@link TimeProvider}.
     *
     * @param provider the provider on which to schedule the execution
     * @return whether the switch to the {@link TimeProvider} was successful
     */
    default boolean runOnClock(TimeProvider provider) {
        return getClockType().runOn(provider);
    }

    /**
     * Describes the different mechanics a {@link Clockable} can implement.
     */
    class ClockType {

        /**
         * Run on the given {@link TimeProvider}, and return whether
         * the switch to the {@link TimeProvider} was successful.
         *
         * The default implementation returns false, meaning it does
         * not change anything and reports the switch as being unsuccessful.
         *
         * @param timeProvider the clock to switch to
         * @return whether the switch was successful
         */
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

        /**
         * Subclass describing some behavior run on a schedule. Requires a callback
         * to be supplied which is executed for the {@link #runOn(TimeProvider)} function,
         * and whose result ist returned.
         */
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
