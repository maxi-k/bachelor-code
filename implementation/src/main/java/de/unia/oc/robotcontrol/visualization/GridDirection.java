/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

public enum GridDirection {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    public GridDirection cycle() {
        GridDirection[] vals = values();
        return vals[(this.ordinal() + 1) % vals.length];
    }
}
