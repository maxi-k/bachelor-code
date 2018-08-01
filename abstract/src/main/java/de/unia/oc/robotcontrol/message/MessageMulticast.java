/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.FlowableMulticast;

/**
 * Dispatcher that can associates MessageTypes with Recipients.
 * @param <T>
 */
public interface MessageMulticast<T extends Message>
        extends FlowableMulticast<MessageType<T>, T> {

    @SuppressWarnings("unchecked")
    default void multicast(T msg) {
        multicast(msg.getType(), msg);
    }
}
