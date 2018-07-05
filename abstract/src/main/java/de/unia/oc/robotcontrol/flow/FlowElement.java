/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

public interface FlowElement<I extends InFlow, O extends OutFlow>
        extends InFlowElement<I>, OutFlowElement<O> {

}
