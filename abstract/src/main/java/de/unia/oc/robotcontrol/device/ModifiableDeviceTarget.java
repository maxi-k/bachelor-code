/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import java.util.UUID;

public interface ModifiableDeviceTarget extends DeviceTarget {

    DeviceTarget setDeviceUUID(UUID uuid);
}
