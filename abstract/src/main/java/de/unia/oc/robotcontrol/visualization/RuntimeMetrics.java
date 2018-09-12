/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import de.unia.oc.robotcontrol.util.Registry;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;

import java.util.function.Consumer;

/**
 * A specific visualization which draws some system metrics at runtime,
 * using reactive-streams to model the succession of values from one
 * given metric.
 *
 * The metrics are registered dynamically using {@link #registerCallback(Object)}
 * (implicit, hidden publisher) or {@link #register(Object, Object)}
 * (explicitly passed publisher as second argument), and differentiated
 * using the generic {@link Type} argument.
 * The data passed through the (implicit or explicit) publisher is used for
 * the y-axis of the given graph. What is used for the x-axis is implementation specific -
 * it could be numbered or timestamped.
 *
 * @param <Type> the type which acts as key to register metrics under
 * @param <Context> the context passed to the {@link Visualization} class
 */
public interface RuntimeMetrics<Type extends Object, Context extends Object>
        extends
        Visualization<Context>,
        Registry<Type, Publisher<Double>> {

    /**
     * The metric which is currently selected by the user.
     * @return the currently selected metric, or {@code null} if there is none.
     */
    @Nullable Type getSelectedMetric();

    /**
     * Sets the currently selected metric.
     * @param metric the metric to select
     */
    void selectMetric(Type metric);

    /**
     * Register a metric identified by {@link Type},
     * and return a callback which can be passed values
     * to be added to the metric (and then drawn).
     *
     * @param metric the metric to select
     * @return a callback which consumes values and
     *  adds them to that specific metric
     */
    Consumer<Double> registerCallback(Type metric);

}
