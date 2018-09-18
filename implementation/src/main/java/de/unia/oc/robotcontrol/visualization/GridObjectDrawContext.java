/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import java.awt.*;

/**
 * The context used in the {@link GridObject#draw(Graphics, GridObjectDrawContext)}
 * method to allow it to correctly display.
 */
public class GridObjectDrawContext {

    /**
     * The x-scale of the grid, that is,
     * the factor of cell width to overall size.
     */
    private final float scaleX;
    /**
     * The x-scale of the grid, that is,
     * the factor of cell height to overall size.
     */
    private final float scaleY;

    GridObjectDrawContext(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    /**
     * Compute the minimum x coordinate for the given
     * grid object's x-cell-coordinate
     * @param objectX the x-cell-coordinate to transform into an absolute coordinate
     * @return the minimum absolute x-coordinate of the given cell number
     */
    public int getXMin(int objectX) {
        return (int) scaleX * objectX;
    }

    /**
     * Compute the maximum x coordinate for the given
     * grid object's x-cell-coordinate
     * @param objectX the x-cell-coordinate to transform into an absolute coordinate
     * @return the maximum absolute x-coordinate of the given cell number
     */
    public int getXMax(int objectX) {
        return (int) scaleX * (objectX + 1) - 1;
    }

    /**
     * Compute the minimum y coordinate for the given
     * grid object's y-cell-coordinate
     * @param objectY the y-cell-coordinate to transform into an absolute coordinate
     * @return the minimum absolute y-coordinate of the given cell number
     */
    public int getYMin(int objectY) {
        return (int) scaleY * objectY;
    }

    /**
     * Compute the maximum y coordinate for the given
     * grid object's y-cell-coordinate
     * @param objectY the y-cell-coordinate to transform into an absolute coordinate
     * @return the maximum absolute y-coordinate of the given cell number
     */
    public int getYMax(int objectY) {
        return (int) scaleY * (objectY + 1) - 1;
    }

    /**
     * @return the x-scale-factor {@link #scaleX}
     */
    public int getWidth() {
        return (int) scaleX;
    }

    /**
     * @return the y-scale-factor {@link #scaleY}
     */
    public int getHeight() {
        return (int) scaleY;
    }
}
