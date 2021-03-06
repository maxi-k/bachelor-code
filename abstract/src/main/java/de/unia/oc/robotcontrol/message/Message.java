/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.Encoding;

/**
 * A message that has a type which can encode it,
 * to be sent to a device, and which can decode it
 * from bytes when it was received from a device.
 * @param <T> The type of the message instance returned / used by encoding / decoding
 */
public interface Message<T extends Message> {

    /**
     * The type of this message which doubles
     * as the instance of {@link Encoding} which can
     * encode and decode messages of this type.
     *
     * @return The {@link MessageType} of this Message
     */
    MessageType<T> getType();

    /**
     * Returns the creation time of this message, see
     * {@link System#currentTimeMillis()}
     *
     * @return The creation time of this piece of sensor data
     */
    long getCreationTime();
}
