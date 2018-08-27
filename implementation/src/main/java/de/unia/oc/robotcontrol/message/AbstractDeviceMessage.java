/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.device.ModifiableDeviceTarget;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractDeviceMessage<T extends Message>
        extends AbstractMessage<T>
        implements ModifiableDeviceTarget {

    private volatile @MonotonicNonNull UUID deviceUUID;

    @Override
    public void setDeviceUUID(UUID uuid) {
        this.deviceUUID = uuid;
    }

    @Override
    public @Nullable UUID getDeviceUUID() {
        return this.deviceUUID;
    }

    @Override
    @SuppressWarnings("nullness")
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof AbstractDeviceMessage)) return false;
        AbstractDeviceMessage m = (AbstractDeviceMessage) o;
        return m == this || (Objects.equals(deviceUUID, m.deviceUUID));
    }
}
