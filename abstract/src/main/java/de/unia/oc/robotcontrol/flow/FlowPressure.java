/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

/**
 * Describes something which has a defined {@link PressureType},
 * that is, either actively (push/pull) or passively (accept/provide)
 * accepts or provides items.
 */
public interface FlowPressure {

    enum PressureType {
        ACTIVE,
        PASSIVE
    }

    PressureType getFlowPressure();
}
