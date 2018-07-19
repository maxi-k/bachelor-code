/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.InFlow;
import de.unia.oc.robotcontrol.flow.OutFlow;
import de.unia.oc.robotcontrol.util.Dispatcher;
import de.unia.oc.robotcontrol.util.Registry;

/**
 * Dispatcher that can associates MessageTypes with Recipients.
 * @param <T>
 */
public interface MessageDispatcher<T extends Message<T>, IF extends InFlow, OF extends OutFlow>
        extends
        MessageSender<T, OF>,
        MessageRecipient<T, IF>,
        Registry<MessageType<T>, T>,
        Dispatcher<T, IF, OF>
{
    @Override
    void dispatch(T msg) throws ItemNotRegisteredException;
}
