package de.unia.oc.robotcontrol.visualization;/* %FILE_TEMPLATE_TEXT% */

import de.unia.oc.robotcontrol.concurrent.Clock;
import de.unia.oc.robotcontrol.concurrent.EmittingClock;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import javax.swing.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class GraphingRuntimeMetricsTest {

    @Test
    void displays() throws InterruptedException {
        GraphingRuntimeMetrics<String> metrics = GraphingRuntimeMetrics.create();
        VisualizingWindow window = new VisualizingWindow(metrics);

        SwingUtilities.invokeLater(window::setup);

        Thread.sleep(1000);
    }

    @Test
    void displaysMetric() throws InterruptedException {
        GraphingRuntimeMetrics<String> metrics = GraphingRuntimeMetrics.create();
        VisualizingWindow window = new VisualizingWindow(metrics);

        SwingUtilities.invokeLater(window::setup);

        Thread.sleep(1000);

        Clock timer1 = EmittingClock.create(Duration.ofMillis(100));
        metrics.register("Time1", Flux.from(timer1.getTicks()).map(Double::new));

        Clock timer2 = EmittingClock.create(Duration.ofMillis(500));
        metrics.register("Time2", Flux.from(timer2.getTicks()).map(Double::new));

        Thread.sleep(20000);
    }

}