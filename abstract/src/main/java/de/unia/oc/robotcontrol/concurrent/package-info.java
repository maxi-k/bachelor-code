/* %FILE_TEMPLATE_TEXT% */
/**
 * Module providing abstractions for concurrency for use in the
 * rest of the framework; most notably used by {@link de.unia.oc.robotcontrol.flow.Flowable}
 *
 * Provides the abstract {@link de.unia.oc.robotcontrol.concurrent.Concurrent} for
 * Classes which explicitly define their own concurrency behavior.
 *
 * There is also {@link de.unia.oc.robotcontrol.concurrent.TimeProvider} and
 * {@link de.unia.oc.robotcontrol.concurrent.Clockable}, which serve the purpose of
 * scheduling the task execution of some {@link de.unia.oc.robotcontrol.concurrent.Concurrent}
 * Class using reactive-streams.
 * This mechanic is predominantly used by system parts that emit data,
 * such as devices and data processors.
 *
 * @author Maximilian Kuschewski
 * @since 1.0
 */
package de.unia.oc.robotcontrol.concurrent;