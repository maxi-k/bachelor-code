/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.flow.InFlow;
import de.unia.oc.robotcontrol.flow.OutFlow;
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
public interface Device<T extends Message, IF extends InFlow, OF extends OutFlow>
        extends MessageRecipient<T, IF>, MessageSender<T, OF> {

}
