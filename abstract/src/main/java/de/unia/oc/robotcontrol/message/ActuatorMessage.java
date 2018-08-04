/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.device.ModifiableDeviceTarget;

public interface ActuatorMessage<T extends ActuatorMessage> extends Message<T>, ModifiableDeviceTarget {

}
