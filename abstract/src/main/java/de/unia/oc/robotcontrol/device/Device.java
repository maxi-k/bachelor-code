/* 2016 */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.flow.FlowDescriptor;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.message.MessageRecipient;
import de.unia.oc.robotcontrol.message.MessageSender;

public interface Device<T extends Message>
        extends FlowDescriptor, MessageSender<T>, MessageRecipient<T> {

}
