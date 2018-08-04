/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

public abstract class AbstractActuatorMessage<T extends ActuatorMessage>
        extends AbstractDeviceMessage<T>
        implements ActuatorMessage<T> {
}
