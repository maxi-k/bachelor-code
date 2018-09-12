/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Describes something that can be terminated, in other words,
 * something whose lifecycle includes an end.
 */
public interface Terminable {

    /**
     * After this has stopped running or was terminated, return true.
     * It must not return false again after having returned true once,
     * that is, the termination must be permanent for any given object.
     *
     * @return {@code true} if this has stopped running or was terminated
     *         {@code false} if it is still running and has not been termianted
     */
    boolean isTerminated();

    /**
     * Terminate this.
     * Tasks include the stopping of running executions,
     * cleaning up of resources etc.
     */
    void terminate();

    /**
     * Create a {@link Terminable} using a callback for determining the
     * result of {@link #isTerminated()}, and a callback for the actual
     * termination in {@link #terminate()}
     *
     * @param isTerminated callback returning whether this is already terminated
     * @param terminate callback for actually terminating. Is only called in
     *                  {@link #terminate()} if {@link #isTerminated()} returns
     *                  false at that point in time.
     * @return a new instance of {@link Terminable}
     */
    static Terminable create(Supplier<Boolean> isTerminated, Runnable terminate) {
        return new Terminable() {
            @Override
            public boolean isTerminated() {
                return isTerminated.get();
            }

            @Override
            public void terminate() {
                if (!isTerminated()) terminate.run();
            }
        };
    }

    /**
     * Subinterface of {@code Terminable}, providing functionality
     * for terminating something on a timeout
     */
    interface TimedTerminable extends Terminable {

        /**
         * Terminate on a given timeout.
         * Should implement the same semantics as
         * {@link java.util.concurrent.ExecutorService#awaitTermination(long, TimeUnit)}, that is
         *
         * Blocks until all tasks have completed execution after a shutdown
         * request, or the timeout occurs, or the current thread is
         * interrupted, whichever happens first.
         *
         * @param time the maximum time to wait
         * @param unit the time unit of the timeout argument
         * @return {@code true} if this executor terminated and
         *         {@code false} if the timeout elapsed before termination
         * @throws InterruptedException if interrupted while waiting
         */
        boolean terminate(@Positive long time, @NonNull TimeUnit unit) throws InterruptedException;

        default boolean terminate(Duration time) throws InterruptedException {
           return terminate(time.toMillis(), TimeUnit.MILLISECONDS);
        }

    }

    /**
     * Implementation of {@link Terminable} that is already
     * (thus constantly) terminated:
     *
     * {@link #isTerminated()} always returns true
     * {@link #terminate()} does nothing
     * {@link #terminate(long, TimeUnit)} always returns true and does nothing
     */
    final class ConstantlyTerminated implements TimedTerminable {

        public static ConstantlyTerminated create() {
            return new ConstantlyTerminated();
        }

        @Override
        public boolean isTerminated() {
            return true;
        }

        @Override
        public void terminate() {}

        @Override
        public boolean terminate(@Positive long time, @NonNull TimeUnit unit) {
            return true;
        }
    }

}
