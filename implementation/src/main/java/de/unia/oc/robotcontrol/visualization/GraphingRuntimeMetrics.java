/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import com.google.common.collect.ImmutableList;
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
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * An implementation of {@link RuntimeMetrics} which displays the
 * metrics using the XChart library on the GUI.
 * @param <Type> the type to register Metrics under
 */
public class GraphingRuntimeMetrics<Type extends Object>
        implements RuntimeMetrics<Type, Component> {

    /**
     * The currently selected metric (by the user)
     */
    private volatile @Nullable Type selectedMetric;

    /**
     * The map storing all value providers registered
     * for the types of metrics.
     */
    private final ConcurrentMap<Type, Flux<Tuple<Long, Double>>> valueProviders;
    /**
     * The maximum number of values cached for each metric for display.
     */
    private final int maxValues = 1 << 12; // about 4000

    /**
     * Display wrapper for an empty screen (no metric selected)
     */
    private final EmptyDrawer drawEmpty;
    /**
     * Display wrapper for showing the currently selected
     * metric.
     */
    private  @MonotonicNonNull MetricDrawer drawMetric;
    /**
     * Display wrapper for showing the drawer which allows
     * the user to select a specific metric for display.
     */
    private final MetricSelectorDrawer drawMetricSelector;

    /**
     * The execution context this runs on.
     */
    private final Scheduler scheduler;
    private volatile boolean addedToParent = false;

    /**
     * The JPanel this is contained in.
     */
    private final JPanel container;

    /**
     * Creates a new instance of the runtime metrics with
     * its own thread and backed by a {@link ConcurrentHashMap}.
     */
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

    /**
     * {@inheritDoc}
     * @return the currently selected metric.
     */
    @Override
    public @Nullable Type getSelectedMetric() {
        return selectedMetric;
    }

    /**
     * {@inheritDoc}
     *
     * Select a new metric, probably indirectly
     * called by the user through some
     * GUI-Event.
     * @param metric the metric to select
     */
    @Override
    public void selectMetric(Type metric) {
        this.selectedMetric = metric;
        updateContainer(metric);
    }

    /**
     * {@inheritDoc}
     *
     * Register a callback for the given type of metric,
     * which can be used to register values to it.
     *
     * @param metric the metric to select
     * @return a consumer which accepts value-points and
     * inserts them into the respective metric.
     */
    @Override
    public Consumer<Double> registerCallback(Type metric) {
        DirectProcessor<Double> processor = DirectProcessor.create();
        register(metric, processor);
        return processor::onNext;
    }

    @Override
    public boolean register(Type key, Publisher<Double> stream) {
        Flux<Tuple<Long, Double>> windowed = Flux
                .from(stream)
                .publishOn(scheduler)
                .index()
                .map((v) -> Tuple.create(v.getT1(), v.getT2()))
                .cache(maxValues);

        this.valueProviders.compute(key, (type, old) -> {
            if (old == null) {
                return windowed.share();
            }
            return old.mergeWith(windowed).share();
        });

        synchronized (this) {
            selectMetric(key);
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

    /**
     * {@inheritDoc}
     *
     * Uses {@link #drawEmpty} and {@link #drawMetric} to
     * produce the content of the window depending on whether
     * a metric was selected or not
     *
     * @param g the graphics
     * @param context the context to use for drawing
     * @param <R> the context used for drawing different
     *           things based on the passed situation
     */
    @Override
    public <R extends Component> void draw(Graphics g, R context) {
        if (this.selectedMetric == null) {
            drawEmpty.drawEmpty(g, container);
        } else if (this.drawMetric != null) {
            drawMetric.drawMetric(g, container);
        }
    }

    /**
     * Update the container by setting the selected metric to the given {@link Type}
     * and redrawing the window
     * @param metric the metric to select.
     */
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

    /**
     * Internal utility class for drawing the content of the window
     * when no metric is selected (presumably because there are none)
     */
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

    @Override
    public String getVisualizationName() {
        return "Graphical Runtime Metrics";
    }

    /**
     * Internal utility class for drawing the window content
     * when a metric is selected. Uses {@link MetricSelectorDrawer} internally
     * to draw the selector.
     */
    private class MetricDrawer implements Terminable {

        private final Disposable subscription;
        private final XChartPanel panel;
        private final Type metric;

        private final Object arrayLock = new Object();
        private final LinkedList<Double> times;
        private final LinkedList<Double> values;

        private MetricDrawer(Type metric) {
            synchronized (arrayLock) {
                times = new LinkedList<>();
                values = new LinkedList<>();
            }

            this.metric = metric;
            Flux<Tuple<Long, Double>> dataFlow = valueProviders.get(metric);
            Chart chart = QuickChart.getChart(metric.toString(), "t", metric.toString(), metric.toString(), new double[] { 0 }, new double[] { 0 });
            chart.getStyleManager().setXAxisLabelRotation(45);
            this.panel = new XChartPanel(chart);
            subscription = dataFlow == null ? (() -> {}) : dataFlow.subscribe(this::updateData);
        }

        private synchronized void init(Container container) {
            drawMetricSelector.init(container);
            container.add(panel, BorderLayout.CENTER);
        }

        @RequiresNonNull({"this.panel", "this.metric", "this.times", "this.values"})
        @SuppressWarnings("nullness")
        private void updateData(@UnknownInitialization MetricDrawer this, Tuple<Long, Double> value) {
            synchronized (arrayLock) {
                int remove = times.size() - maxValues;
                synchronized (times) {
                    if (remove >= 0) {
                        times.removeFirst();
                    }
                    times.add((double) value.first);
                }
                synchronized (values) {
                    if (remove >= 0) {
                        values.removeFirst();
                    }
                    values.add(value.second);
                }
            }

            panel.updateSeries(metric.toString(),
                    ImmutableList.copyOf(times),
                    ImmutableList.copyOf(values),
                    null);

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

    /**
     * Internal utility class for displaying a selector to the user,
     * which allows him to select a metric to draw.
     */
    private class MetricSelectorDrawer {

        private final JComboBox<Type> selector;
        private final ItemListener listener;

        private MetricSelectorDrawer() {
            this.selector = new JComboBox<>();
            this.listener = (e) -> {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    Type item = selector.getItemAt(selector.getSelectedIndex());
                    if (selectedMetric != item) {
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
