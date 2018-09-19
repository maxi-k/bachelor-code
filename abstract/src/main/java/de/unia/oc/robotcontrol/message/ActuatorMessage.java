/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.device.ModifiableDeviceTarget;

/**
 * A specific message type semantically directed towards devices,
 * targeting some specific actuator.
 *
 * @param <T> the actual message type
 */
public interface ActuatorMessage<T extends ActuatorMessage> extends Message<T>, ModifiableDeviceTarget {

}
