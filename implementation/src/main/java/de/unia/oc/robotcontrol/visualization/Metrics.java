/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import java.awt.*;

/**
 * Class with utility methods for the visualization module of
 * the project ({@link de.unia.oc.robotcontrol.visualization})
 * ("module-class").
 *
 * This only contains static methods and cannot be instantiated.
 */
@SuppressWarnings("initialization")
public class Metrics {

    private Metrics() {}

    /**
     * If there are graphics, don't use metrics.
     * Otherwise, use graphical metrics.
     */
    private static volatile MetricOutput OUTPUT = GraphicsEnvironment.isHeadless()
            ? MetricOutput.NONE
            : MetricOutput.GRAPHICAL;

    /**
     * Lock for the {@link #INSTANCE} field
     */
    private static final Object instanceLock = new Object();
    /**
     * The {@link RuntimeMetrics} instance as returned by {@link #instance()},
     * for a common place to refer to a single metrics instance. Initialized
     * lazily.
     */
    private static RuntimeMetrics<String, ?> INSTANCE;

    /**
     * Returns the singular instance of {@link RuntimeMetrics} that
     * can be used throughout the system.
     *
     * Depending on the value of {@link #OUTPUT}, returns
     * {@link GraphingRuntimeMetrics} or {@link IgnoringRuntimeMetrics}.
     *
     * @return an instance of {@link RuntimeMetrics}
     */
    public static RuntimeMetrics<String, ?> instance() {
        synchronized (instanceLock) {
            if (INSTANCE == null) {
                switch (OUTPUT) {
                    case GRAPHICAL:
                        GraphingRuntimeMetrics<String> metrics = GraphingRuntimeMetrics.create();
                        INSTANCE = metrics;
                        new VisualizingWindow(metrics).setup();
                        break;
                    default:
                        INSTANCE = IgnoringRuntimeMetrics.create();
                        break;
                }
            }
            return INSTANCE;
        }
    }

    /**
     * @return the current {@link MetricOutput}
     */
    public static MetricOutput getOutput() {
        return OUTPUT;
    }

    /**
     * Set the current {@link #OUTPUT} to the given value
     * if the {@link RuntimeMetrics} instance given by {@link #instance()}
     * has not been initialized yet.
     * @param output the {@link MetricOutput} to set {@link #OUTPUT} to
     * @return whether the metric has been set.
     */
    public static boolean setOutput(MetricOutput output) {
        synchronized (instanceLock) {
            if (INSTANCE != null) {
                return false;
            }
            OUTPUT = output;
            return true;
        }
    }

    /**
     * Different types of outputs for {@link RuntimeMetrics}
     */
    public enum MetricOutput {
        GRAPHICAL,
        NONE
    }
}
