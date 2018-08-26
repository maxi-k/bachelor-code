/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import de.unia.oc.robotcontrol.util.Registry;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;

public interface RuntimeMetrics<Type extends Object, Context extends Object>
        extends
        Visualization<Context>,
        Registry<Type, Publisher<Double>> {

    @Nullable Type getSelectedMetric();

    void selectMetric(Type metric);

}
