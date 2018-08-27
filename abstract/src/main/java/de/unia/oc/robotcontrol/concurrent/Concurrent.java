/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import de.unia.oc.robotcontrol.flow.Flowable;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Describes an Object that is aware of concurrency.
 *
 * Objects that implement this should make sure they
 * are thread-safe.
 */
public interface Concurrent {

    /**
     * What concurrency paradigm does this Object follow?
     * Does it handle concurrent execution itself, or does
     * it want the system to handle it?
     *
     * This function must return the same value for the same object
     * at all times.
     *
     * @return a (constant) {@link ConcurrencyType}
     */
    @Pure
    ConcurrencyType getConcurrencyType();

    enum ConcurrencyType {

        /**
         * Concurrency is handled internally, that is, the Object
         * knows on which threads its tasks should be executed and
         * handles method calls and Flow {@link Flowable} accordingly.
         */
        INTERNAL,

        /**
         * Concurrency is handled externally, that is, the Object
         * does not want to know where its tasks should be executed,
         * and the system has to make sure method calls and Flow
         * {@link Flowable} are executed correctly.
         */
        EXTERNAL
    }
}
