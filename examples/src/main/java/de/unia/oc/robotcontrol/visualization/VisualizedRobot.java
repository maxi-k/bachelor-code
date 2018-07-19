/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import java.awt.*;

public class VisualizedRobot extends GridObject {

    private char command = 's';

    @Override
    public void draw(Graphics g, GridObjectDrawContext context) {
        super.draw(g, context);
        g.drawString(String.valueOf(command),
                context.getXMin(getX()) + context.getWidth() / 2,
                context.getYMin(getY()) + context.getHeight() / 2);
    }

    @Override
    protected Color getColor() {
        return Color.CYAN;
    }
}
