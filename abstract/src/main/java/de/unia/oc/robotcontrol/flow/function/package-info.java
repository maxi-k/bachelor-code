/* %FILE_TEMPLATE_TEXT% */
/**
 * Defines different functional transformations for the reactive-streams
 * abstractions, using three functional interfaces (subtype of {@link java.util.function.Function}
 * for the three typed reactive-streams interfaces:
 *
 * - {@link org.reactivestreams.Publisher} by {@link de.unia.oc.robotcontrol.flow.function.PublisherTransformation}
 * - {@link org.reactivestreams.Subscriber} by {@link de.unia.oc.robotcontrol.flow.function.SubscriberTransformation}
 * - {@link org.reactivestreams.Processor} by {@link de.unia.oc.robotcontrol.flow.function.ProcessorTransformation}
 *
 * Also implements some static methods for "lifting" functions {@code (A -> B)} to the each
 * reactive-streams abstraction, i.e for example {@code (A -> B) -> (Publisher<A> -> Publisher<B>)},
 * akin to a Functor.
 *
 * @author Maximilian Kuschewski
 * @since 1.0
 */
package de.unia.oc.robotcontrol.flow.function;
