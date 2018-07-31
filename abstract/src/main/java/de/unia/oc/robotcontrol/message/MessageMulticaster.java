/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.FlowableMulticast;

/**
 * Dispatcher that can associates MessageTypes with Recipients.
 * @param <T>
 */
public interface MessageMulticaster extends FlowableMulticast<MessageType, Message> {

    default void multicast(Message msg) {
        multicast(msg.getType(), msg);
    }
}
