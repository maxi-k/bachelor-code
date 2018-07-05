/* 2016 */
package de.unia.oc.robotcontrol.message;

/**
 * A message that has a type which can encode it,
 * to be sent to a device, and which can decode it
 * from bytes when it was received from a device.
 * @param <T> The type of the message instance returned / used by encoding / decoding
 */
public interface Message<T extends Message> {

    /**
     *
     * @return The {@link MessageType} of this Message
     */
    MessageType<T> getType();

}
