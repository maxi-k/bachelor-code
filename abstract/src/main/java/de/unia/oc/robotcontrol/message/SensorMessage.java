/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.device.ModifiableDeviceTarget;

public interface SensorMessage<T extends Message> extends Message<T>, ModifiableDeviceTarget {
}
