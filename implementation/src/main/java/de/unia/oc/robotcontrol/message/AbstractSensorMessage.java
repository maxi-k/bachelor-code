/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;


/**
 * Abstract superclass for a sensor message.
 * Utility class simplifying the creation of concrete
 * sensor message implementations.
 * @param <T> the concrete type of message this represents
 */
public abstract class AbstractSensorMessage<T extends SensorMessage>
        extends AbstractDeviceMessage<T>
        implements SensorMessage<T> {
}
