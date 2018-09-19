/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import javax.swing.*;
import java.awt.*;

/**
 * An implementation of {@link JPanel} that
 * draws a {@link Visualization} using {@link Visualization#draw(Graphics, Object)}
 * in the {@link #paintComponent(Graphics)} method.
 */
public class VisualizingPanel extends JPanel {

    private final Visualization<Component> toVisualize;

    VisualizingPanel(Visualization<Component> toVisualize) {
        this.toVisualize = toVisualize;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        toVisualize.draw(graphics, this);
    }
}
