/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import de.unia.oc.robotcontrol.flow.InFlow;
import de.unia.oc.robotcontrol.flow.InFlowElement;
import de.unia.oc.robotcontrol.flow.OutFlow;
import de.unia.oc.robotcontrol.flow.OutFlowElement;

/**
 * Generic interface for a dispatcher
 * @param <T> the type of element to dispatch
 */
public interface Dispatcher<T, IF extends InFlow, OF extends OutFlow>
        extends InFlowElement<IF>, OutFlowElement<OF> {

    /**
     * Dispatches the given element.
     * @param msg the element to dispatch
     * @throws IllegalArgumentException if the element is not eligible for dispatching
     */
    void dispatch(T msg) throws IllegalArgumentException;

}
