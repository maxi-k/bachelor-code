/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.device.DeviceTarget;
import de.unia.oc.robotcontrol.device.ModifiableDeviceTarget;

import java.util.UUID;

public interface SensorMessage<T extends Message> extends Message<T>, ModifiableDeviceTarget {

    @Override
    SensorMessage<T> setDeviceUUID(UUID uuid);
}
