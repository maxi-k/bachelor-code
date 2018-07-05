/* 2016 */
package de.unia.oc.robotcontrol.flow;

public interface FlowDescriptor {

    FlowStrategy getFlowType();

    ProviderStrategy getProviderStrategy();
}
