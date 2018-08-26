/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import java.awt.*;

@SuppressWarnings("initialization")
public class Metrics {

    private static final Object instanceLock = new Object();
    private static GraphingRuntimeMetrics<String> INSTANCE;
    private static VisualizingWindow window;

    public static RuntimeMetrics<String, Component> instance() {
        synchronized (instanceLock) {
            if (INSTANCE == null) {
                INSTANCE = GraphingRuntimeMetrics.create();
                window = new VisualizingWindow(INSTANCE);
                window.setup();
            }
            return INSTANCE;
        }
    }
}
