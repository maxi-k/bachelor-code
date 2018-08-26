/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.QuickChart;
import com.xeiam.xchart.XChartPanel;
import de.unia.oc.robotcontrol.concurrent.Terminable;
import de.unia.oc.robotcontrol.util.Tuple;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.checkerframework.dataflow.qual.Pure;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GraphingRuntimeMetrics<Type extends Object>
        implements RuntimeMetrics<Type, Component> {

    private volatile @Nullable Type selectedMetric;

    private final ConcurrentMap<Type, Flux<Tuple<Long, Double>>> valueProviders;
    private final int maxValues = 500;

    private final EmptyDrawer drawEmpty;
    private  @MonotonicNonNull MetricDrawer drawMetric;
    private final MetricSelectorDrawer drawMetricSelector;

    private final Scheduler scheduler;
    private volatile boolean addedToParent = false;
    private final JPanel container;

    private GraphingRuntimeMetrics() {
        this.scheduler = Schedulers.newSingle("GraphingRuntimeMetrics");
        this.valueProviders = new ConcurrentHashMap<>();

        this.drawEmpty = new EmptyDrawer();
        this.drawMetricSelector = new MetricSelectorDrawer();

        this.container = new JPanel();
        container.setLayout(new BorderLayout());
        updateContainer(null);
    }

    public static <T extends Object> GraphingRuntimeMetrics<T> create() {
        return new GraphingRuntimeMetrics<>();
    }

    @Override
    public @Nullable Type getSelectedMetric() {
        return selectedMetric;
    }

    @Override
    public void selectMetric(Type metric) {
        this.selectedMetric = metric;
        updateContainer(metric);
    }

    @Override
    public boolean register(Type key, Publisher<Double> stream) {
        Flux<Tuple<Long, Double>> windowed = Flux
                .from(stream)
                .publishOn(scheduler)
                .map((v) -> Tuple.create(System.currentTimeMillis(), v));

        this.valueProviders.compute(key, (type, old) -> {
            if (old == null) {
                return windowed.share();
            }
            return old.mergeWith(windowed).share();
        });

        synchronized (this) {
            if (selectedMetric == null) {
                selectMetric(key);
            }
        }

        return true;
    }

    @Override
    public Optional<Publisher<Double>> getValueFor(Type key) {
        Flux<Tuple<Long, Double>> val = this.valueProviders.get(key);
        if (val == null) {
            return Optional.empty();
        }
        return Optional.of( val.map(Tuple::getSecond) );
    }

    @Override
    public Optional<Type> getKeyFor(Publisher<Double> value) {
        return Optional.empty();
    }

    @Override
    public <R extends Component> void draw(Graphics g, R context) {
        if (this.selectedMetric == null) {
            drawEmpty.drawEmpty(g, container);
        } else if (this.drawMetric != null) {
            drawMetric.drawMetric(g, container);
        }
    }

    @RequiresNonNull({"this.container", "this.drawEmpty"})
    @SuppressWarnings("nullness")
    private void updateContainer(@UnknownInitialization GraphingRuntimeMetrics<Type> this, @Nullable Type metric) {
        SwingUtilities.invokeLater(() -> {
            synchronized (this) {
                container.removeAll();
                if (metric == null) {
                    drawEmpty.init(container);
                } else {
                    if (this.drawMetric != null) this.drawMetric.terminate();
                    this.drawMetric = new MetricDrawer(metric);
                    drawMetric.init(container);
                }
                container.revalidate();
                container.repaint();
            }
        });
    }

    @Override
    public JPanel getPanel() {
        return this.container;
    }

    @Override
    @Pure
    @EnsuresNonNullIf(expression = "getPanel()", result = true)
    @SuppressWarnings("nullness")
    public boolean hasOwnPanel() {
        return true;
    }

    private class EmptyDrawer {

        private final JLabel text;

        private EmptyDrawer() {
            this.text = new JLabel("No metrics available.");
            text.setHorizontalAlignment(SwingConstants.CENTER);
            text.setVerticalAlignment(SwingConstants.CENTER);
        }

        private void init(Container container) {
            container.add(text);
        }

        private <R extends Component> void drawEmpty(Graphics g, R context) {
        }

    }

    private class MetricDrawer implements Terminable {

        private final Disposable subscription;
        private final XChartPanel panel;
        private final Type metric;

        private final ArrayList<Double> times;
        private final ArrayList<Double> values;

        private MetricDrawer(Type metric) {
            times = new ArrayList<>(maxValues);
            values = new ArrayList<>(maxValues);

            this.metric = metric;
            Flux<Tuple<Long, Double>> dataFlow = valueProviders.get(metric);
            Chart chart = QuickChart.getChart(metric.toString(), "t", metric.toString(), metric.toString(), new double[] { 0 }, new double[] { 0 });
            this.panel = new XChartPanel(chart);
            subscription = dataFlow == null ? (() -> {}) : dataFlow.subscribe(this::updateData);
        }

        private synchronized void init(Container container) {
            drawMetricSelector.init(container);
            container.add(panel, BorderLayout.CENTER);
        }

        @RequiresNonNull({"this.panel", "this.metric", "this.times", "this.values"})
        @SuppressWarnings("nullness")
        private synchronized void updateData(@UnknownInitialization MetricDrawer this, Tuple<Long, Double> value) {
            times.add((double) value.first);
            values.add(value.second);

            panel.updateSeries(metric.toString(), times, values, null);

            panel.revalidate();
            panel.repaint();
        }

        @Override
        public void terminate() {
            subscription.dispose();
        }

        @Override
        public boolean isTerminated() {
            return subscription.isDisposed();
        }

        private <R extends Component> void drawMetric(Graphics g, R context) {
            drawMetricSelector.drawMetricSelector(g, context);
        }
    }

    private class MetricSelectorDrawer {

        private final JComboBox<Type> selector;
        private final ItemListener listener;

        private MetricSelectorDrawer() {
            this.selector = new JComboBox<>();
            this.listener = (e) -> {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    Type item = selector.getItemAt(selector.getSelectedIndex());
                    if (selectedMetric != item) {
                        System.out.println("action performed!");
                        selectMetric(item);
                    }
                }
            };
            selector.setSize(100, 20);
            selector.setLocation(10, 10);
            if (selectedMetric != null) selector.setSelectedItem(selectedMetric);
        }

        private synchronized void init(Container container) {
            Type toSelect = selectedMetric;
            selector.removeItemListener(listener);
            selector.removeAllItems();

            container.add(selector, BorderLayout.NORTH);
            selector.setVisible(true);

            for (Type t : valueProviders.keySet()) {
                selector.addItem(t);
            }

            if (toSelect != null) selector.setSelectedItem(toSelect);
            selector.addItemListener(listener);

            SwingUtilities.invokeLater(() -> {
                selector.revalidate();
                selector.repaint();
            });
        }

        private <R extends Component> void drawMetricSelector(Graphics g, R context) {

        }
    }

}
