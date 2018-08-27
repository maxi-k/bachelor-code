/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import java.awt.*;

@SuppressWarnings("initialization")
public class Metrics {

    private static volatile MetricOutput OUTPUT = GraphicsEnvironment.isHeadless()
            ? MetricOutput.NONE
            : MetricOutput.GRAPHICAL;

    private static final Object instanceLock = new Object();
    private static RuntimeMetrics<String, ?> INSTANCE;

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

    public static MetricOutput getOutput() {
        return OUTPUT;
    }

    public static boolean setOutput(MetricOutput output) {
        synchronized (instanceLock) {
            if (INSTANCE != null) {
                return false;
            }
            OUTPUT = output;
            return true;
        }
    }

    public enum MetricOutput {
        GRAPHICAL,
        NONE
    }
}
