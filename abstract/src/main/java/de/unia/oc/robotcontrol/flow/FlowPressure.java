/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

public interface FlowPressure {

    enum PressureType {
        ACTIVE,
        PASSIVE
    }

    PressureType getFlowPressure();
}
