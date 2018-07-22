/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import javax.swing.*;
import java.awt.*;

public class VisualizingWindow {

    private final Visualization<Component> toVisualize;
    private volatile JFrame frame;

    public VisualizingWindow(Visualization<Component> v) {
        this.toVisualize = v;
    }

    public void setup() {
        JPanel mainPanel = new VisualizingPanel(toVisualize);

        JFrame frame = new JFrame("DrawRect");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        this.frame = frame;
    }

    public synchronized void update() {
        try {
            this.frame.repaint();
        } catch (NullPointerException e) {
            // frame is not available yet (in this thread),
            // which is ok
        }
    }
}
