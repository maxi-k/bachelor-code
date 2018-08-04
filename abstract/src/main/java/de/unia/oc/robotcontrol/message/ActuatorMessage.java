/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.device.DeviceTarget;
import de.unia.oc.robotcontrol.device.ModifiableDeviceTarget;

import java.util.UUID;

public interface ActuatorMessage<T extends ActuatorMessage> extends Message<T>, ModifiableDeviceTarget {

    @Override
    ActuatorMessage setDeviceUUID(UUID uuid);
}
