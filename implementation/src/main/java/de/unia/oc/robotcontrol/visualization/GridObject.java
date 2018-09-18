/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import java.awt.*;

/**
 * Superclass for an object that is drawn on a {@link ObjectGrid}
 * in a specific grid cell.
 */
public class GridObject implements Visualization<GridObjectDrawContext> {

    /**
     * The cell coordinates that this is on.
     */
    private int x, y;

    /**
     * {@inheritDoc}
     *
     * Default draw method for this {@link GridObject}.
     * Fills the grid cell with the color set by {@link #getColor()}.
     *
     * @param g the graphics
     * @param context the context to use for drawing
     */
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

    /**
     * @return the color this should be drawn in
     */
    protected Color getColor() {
        return Color.darkGray;
    }

    /**
     * @return the x-coordinate of the cell this
     * is positioned in
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y-coordinate of the cell this
     * is positioned in
     */
    public int getY() {
        return y;
    }

    /**
     * Set this objects X-Coordinate
     * @param x the coordinate to set
     */
    void setX(int x) {
        this.x = x;
    }

    /**
     * Set this objects Y-Coordinate
     * @param y the coordinate to set
     */
    void setY(int y) {
        this.y = y;
    }

    /**
     * Set this objects X-Coordinate and Y-Coordinate
     * @param x the x-coordinate to set
     * @param y the y-coordinate to set
     */
    void setXY(int x, int y) {
        setX(x);
        setY(y);
    }
}
