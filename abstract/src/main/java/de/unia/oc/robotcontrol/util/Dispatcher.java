/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import de.unia.oc.robotcontrol.flow.old.InFlowElement;
import de.unia.oc.robotcontrol.flow.old.OutFlowElement;

/**
 * Generic interface for a dispatcher
 * @param <T> the type of element to dispatch
 */
public interface Dispatcher<T> extends InFlowElement, OutFlowElement {

    /**
     * Dispatches the given element.
     * @param msg the element to dispatch
     * @throws IllegalArgumentException if the element is not eligible for dispatching
     */
    void dispatch(T msg) throws IllegalArgumentException;

}
