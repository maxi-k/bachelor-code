/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

import java.util.UUID;

public interface DeviceTarget {

    @Pure
    @Nullable UUID getDeviceUUID();

    @Pure
    default String getDeviceName() {
        return getDeviceUUID() == null
                ? "Unidentified Device"
                : getDeviceUUID().toString();
    }
}
