/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.device.ModifiableDeviceTarget;

/**
 * A specific message type semantically coming from a device,
 * from some concrete Sensor.
 * @param <T> the actual type of the message
 */
public interface SensorMessage<T extends Message> extends Message<T>, ModifiableDeviceTarget {
}
