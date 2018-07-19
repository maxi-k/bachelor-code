/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import java.awt.*;

public class GridObject implements Visualization<GridObjectDrawContext> {

    private int x, y;

    @Override
    public void draw(Graphics g, GridObjectDrawContext context) {
        Color c = g.getColor();
        g.setColor(getColor());

        g.fillRect(
                context.getXMin(x),
                context.getYMin(y),
                context.getWidth(),
                context.getHeight()
        );

        g.setColor(c);
    }

    protected Color getColor() {
        return Color.darkGray;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    void setX(int x) {
        this.x = x;
    }

    void setY(int y) {
        this.y = y;
    }

    void setXY(int x, int y) {
        setX(x);
        setY(y);
    }
}
