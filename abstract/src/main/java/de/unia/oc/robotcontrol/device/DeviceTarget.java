/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.message.Message;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

import java.util.UUID;

/**
 * Abstract interface for something that is connected to a specific device.
 *
 * Examples are:
 * - A {@link Device} itself
 * - A {@link Message} coming from- or directed to a specific device
 * - Logs coming from a specific device
 */
public interface DeviceTarget {

    /**
     * Returns a unique identifier for a given device.
     *
     * @return A instance of {@link UUID} unique between devices
     */
    @Pure
    @Nullable UUID getDeviceUUID();

    /**
     * Returns the Name of the device this targets.
     * Defaults to the {@link #getDeviceUUID()}, or 'Unidentified Device'
     * if the UUID is null.
     * Does not have to be unique.
     *
     * @return a human readable String representing this device.
     */
    @Pure
    default String getDeviceName() {
        return getDeviceUUID() == null
                ? "Unidentified Device"
                : getDeviceUUID().toString();
    }
}
