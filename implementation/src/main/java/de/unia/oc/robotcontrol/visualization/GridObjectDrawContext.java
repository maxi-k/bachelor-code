/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

public class GridObjectDrawContext {

    private final float scaleX;
    private final float scaleY;

    GridObjectDrawContext(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public int getXMin(int objectX) {
        return (int) scaleX * objectX;
    }

    public int getXMax(int objectX) {
        return (int) scaleX * (objectX + 1) - 1;
    }

    public int getYMin(int objectY) {
        return (int) scaleY * objectY;
    }

    public int getYMax(int objectY) {
        return (int) scaleY * (objectY + 1) - 1;
    }

    public int getWidth() {
        return (int) scaleX;
    }
    public int getHeight() {
        return (int) scaleY;
    }
}
