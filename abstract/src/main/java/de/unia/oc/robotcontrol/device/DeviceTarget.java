/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import org.checkerframework.dataflow.qual.Pure;

import java.util.UUID;

public interface DeviceTarget {

    @Pure
    UUID getDeviceUUID();

    @Pure
    default String getDeviceName() {
        return getDeviceUUID().toString();
    }
}
