/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;

import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Implementation of {@link RuntimeMetrics} that ignores all values.
 * May be used when the application is running in a non-gui environment,
 * and there is no good way of displaying them.
 *
 * @param <Type> the type to register metrics under
 */
public class IgnoringRuntimeMetrics<Type extends Object> implements RuntimeMetrics<Type, Object> {

    public static <T extends Object> IgnoringRuntimeMetrics<T> create() {
        return new IgnoringRuntimeMetrics<>();
    }

    @Nullable
    @Override
    public Type getSelectedMetric() {
        return null;
    }

    @Override
    public void selectMetric(Type metric) {

    }

    @Override
    public Consumer<Double> registerCallback(Type metric) {
        return (d) -> {};
    }

    @Override
    public boolean register(Type key, Publisher<Double> value) {
        return false;
    }

    @Override
    public Optional<Publisher<Double>> getValueFor(Type key) {
        return Optional.empty();
    }

    @Override
    public Optional<Type> getKeyFor(Publisher<Double> value) {
        return Optional.empty();
    }

    @Override
    public <R extends Object> void draw(Graphics g, R context) {

    }
}
