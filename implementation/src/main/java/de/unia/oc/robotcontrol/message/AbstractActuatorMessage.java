/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

/**
 * Abstract superclass for an actuator message.
 * Utility class simplifying the creation of concrete
 * actuator message implementations.
 * @param <T> the concrete type of message this represents
 */
public abstract class AbstractActuatorMessage<T extends ActuatorMessage>
        extends AbstractDeviceMessage<T>
        implements ActuatorMessage<T> {
}
