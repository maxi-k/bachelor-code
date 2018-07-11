/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.message.MessageRecipient;
import de.unia.oc.robotcontrol.message.MessageSender;

/**
 * An interface that describes a device, an abstraction
 * for an actual hardware this connects to, which can send
 * and receive messages
 *
 * @param <T> The subtype of Messages this accepts or sends
 */
public interface Device<T extends Message>
        extends MessageSender<T>, MessageRecipient<T> {

}
