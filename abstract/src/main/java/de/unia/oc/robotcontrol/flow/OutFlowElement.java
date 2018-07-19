/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

/**
 * Describes something which has an {@link InFlow}
 */
public interface OutFlowElement<OF extends OutFlow> {

    OF outFlow();
}
