/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

import javax.swing.*;
import java.awt.*;

public interface Visualization<T> {

    <R extends T> void draw(Graphics g, R context);

    @Pure
    @EnsuresNonNullIf(expression = "getPanel()", result = true)
    default boolean hasOwnPanel() {
        return false;
    }

    default @Nullable JPanel getPanel() {
        return null;
    }

    default String getVisualizationName() {
        return "Visualization";
    }

}
