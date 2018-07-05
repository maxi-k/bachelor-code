/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

public interface FlowDescriptor {

    FlowStrategy getFlowType();

    ProviderStrategy getProviderStrategy();
}
