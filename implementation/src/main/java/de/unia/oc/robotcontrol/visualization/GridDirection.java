/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

public enum GridDirection {
    UP,
    RIGHT,
    DOWN,
    LEFT;


    public GridDirection add(GridDirection other) {
        GridDirection[] vals = values();
        return vals[(this.ordinal() + other.ordinal()) % vals.length];
    }

    public GridDirection cycle() {
        return add(RIGHT);
    }

    public GridDirection cycleCC() {
        return add(LEFT);
    }
}
