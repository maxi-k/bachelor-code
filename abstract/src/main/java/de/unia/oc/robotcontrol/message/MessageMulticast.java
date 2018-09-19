/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.FlowableMulticast;

/**
 * Dispatcher that can associates MessageTypes with Recipients.
 * @param <T> The type of message this can receive and will send
 */
public interface MessageMulticast<T extends Message>
        extends FlowableMulticast<MessageType<? extends T>, T> {

    /**
     * Multicast the given message like
     * {@link #multicast(Object, Object)},
     * using the messages message-type as the
     * topic to multicast on.
     * @param msg the message to multicast
     */
    @SuppressWarnings("unchecked")
    default void multicast(T msg) {
        multicast(msg.getType(), msg);
    }
}
