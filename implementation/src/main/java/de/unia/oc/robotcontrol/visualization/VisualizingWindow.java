/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * A utility class that generates a new {@link JFrame} window
 * to display the given {@link Visualization} in.
 * The visualization has to have {@link Component} as its generic
 * draw context passed to {@link Visualization#draw(Graphics, Object)}.
 *
 * After initialization, the {@link #setup()} method has to be called
 * to display the window (presumably in a GUI Thread).
 *
 * To update the window, the {@link #update()} method is called.
 */
public class VisualizingWindow {

    /**
     * The visualization to draw
     */
    private final @NonNull Visualization<Component> toVisualize;
    /**
     * The GUI window to draw into
     */
    private volatile @Nullable JFrame frame;

    /**
     * Create a new instance of {@link VisualizingWindow} for the given
     * {@link Visualization} instance.
     *
     * @param v the {@link Visualization} to display
     */
    @EnsuresNonNull({"toVisualize"})
    public VisualizingWindow(@NonNull Visualization<Component> v) {
        this.toVisualize = v;
    }

    /**
     * Set up the window by displaying it and drawing
     * the {@link #toVisualize} Instance inside.
     * Should generally be called in the Applications GUI Thread.
     *
     * The default behavior is that the window is disposed on closing,
     * but the application is not quit all together, as this is presumed
     * to be used to visualize metrics and such, not to control the application.
     *
     * The name of the visualization as defined by {@link Visualization#getVisualizationName()}
     * is used as the window title.
     */
    @EnsuresNonNull("frame")
    public synchronized void setup() {
        JPanel mainPanel = toVisualize.hasOwnPanel() ? toVisualize.getPanel() : new VisualizingPanel(toVisualize);

        JFrame frame = new JFrame("DrawRect");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setTitle(toVisualize.getVisualizationName());
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.requestFocus();

        this.frame = frame;
    }

    /**
     * Update the GUI window by repainting it.
     */
    public synchronized void update() {
        // frame maybe null if setup() was not called yet
        // or frame has not been synchronized
        if (this.frame != null) {
            this.frame.repaint();
        }
    }
}
