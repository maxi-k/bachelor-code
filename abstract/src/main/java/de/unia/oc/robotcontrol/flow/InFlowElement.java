/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

/**
 * Describes something which has an {@link InFlow}
 */
public interface InFlowElement<IF extends InFlow> {

   IF inFlow();
}
