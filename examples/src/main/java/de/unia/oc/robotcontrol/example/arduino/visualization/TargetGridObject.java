/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.visualization;

import de.unia.oc.robotcontrol.visualization.GridObject;

import java.awt.*;

public class TargetGridObject extends GridObject {

    @Override
    protected Color getColor() {
        return Color.orange;
    }
}
