/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.message.MessageRecipient;
import de.unia.oc.robotcontrol.message.MessageSender;

public interface Device<T extends Message>
        extends MessageSender<T>, MessageRecipient<T> {

}
