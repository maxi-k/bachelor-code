/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import java.awt.*;

public class VisualizingWindow {

    private final @NonNull Visualization<Component> toVisualize;
    private volatile @Nullable JFrame frame;

    @EnsuresNonNull({"toVisualize"})
    public VisualizingWindow(@NonNull Visualization<Component> v) {
        this.toVisualize = v;
    }

    @EnsuresNonNull("frame")
    public synchronized void setup() {
        JPanel mainPanel = toVisualize.hasOwnPanel() ? toVisualize.getPanel() : new VisualizingPanel(toVisualize);

        JFrame frame = new JFrame("DrawRect");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.requestFocus();
        frame.setTitle(toVisualize.getVisualizationName());

        this.frame = frame;
    }

    public synchronized void update() {
        // frame maybe null if setup() was not called yet
        // or frame has not been synchronized
        if (this.frame != null) {
            this.frame.repaint();
        }
    }
}
