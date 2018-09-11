/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

import javax.swing.*;
import java.awt.*;

/**
 * General purpose visualization interface; defines something which
 * can draw itself on {@link Graphics}.
 *
 * @param <T> the type of context to be passed to the draw method
 */
public interface Visualization<T> {

    /**
     * Draws itself using {@link Graphics} and the
     * generic context passed to it.
     *
     * @param g the graphics
     * @param context the context to use for drawing
     * @param <R> the subtype of the context that may be passed
     */
    <R extends T> void draw(Graphics g, R context);

    /**
     * Whether this visualization defines its own {@link Panel}
     * or is just drawn in the context of a provided one.
     * If this is true, {@link #getPanel()} has to be non-null.
     *
     * @return {@link true} if this defines its own {@link Panel},
     *         {@link false} otherwise
     */
    @Pure
    @EnsuresNonNullIf(expression = "getPanel()", result = true)
    default boolean hasOwnPanel() {
        return false;
    }

    /**
     *
     * @return the {@link Panel} instance this holds; if
     *         there is none, returns {@null}
     */
    default @Nullable JPanel getPanel() {
        return null;
    }

    /**
     *
     * @return a human-readable name for this visualization
     */
    default String getVisualizationName() {
        return "Visualization";
    }

}
