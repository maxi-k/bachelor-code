/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import java.awt.*;

public interface Visualization<T> {

    <R extends T> void draw(Graphics g, R context);

}
