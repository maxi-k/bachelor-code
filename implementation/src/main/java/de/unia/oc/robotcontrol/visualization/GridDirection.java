/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

/**
 * Enum representing the different directions
 * possible on a two-dimensional grid
 * as represented by {@link ObjectGrid}
 */
public enum GridDirection {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    /**
     * Rotate the direction by the other direction.
     * @param other
     * @return
     */
    public GridDirection add(GridDirection other) {
        GridDirection[] vals = values();
        return vals[(this.ordinal() + other.ordinal()) % vals.length];
    }

    /**
     * Rotate clock-wise
     * @return a new {@link GridDirection}
     */
    public GridDirection cycle() {
        return add(RIGHT);
    }

    /**
     * Rotate counter-clockwise
     * @return a new {@link GridDirection}
     */
    public GridDirection cycleCC() {
        return add(LEFT);
    }
}
