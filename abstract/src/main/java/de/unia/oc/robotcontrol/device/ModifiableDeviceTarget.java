/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import java.util.UUID;

/**
 * Subinterface of {@link DeviceTarget} that allows for modification
 * of the connected (targeted) device by setting its unique identifier
 * as described by {@link #getDeviceUUID()}
 */
public interface ModifiableDeviceTarget extends DeviceTarget {

    /**
     * Set the unique identifier - and thus the target - of this.
     * @param uuid The unique {@link UUID} to set
     */
    void setDeviceUUID(UUID uuid);
}
