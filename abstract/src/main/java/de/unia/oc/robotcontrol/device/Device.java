/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.concurrent.Terminable;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.message.MessageProcessor;

/**
 * An interface that describes a device, an abstraction
 * for an actual hardware this connects to, which can send
 * and receive messages
 *
 * @param <Output> The subtype of Messages this accepts
 */
public interface Device<Output extends Message>
        extends MessageProcessor<Message, Output>, Terminable {

}
