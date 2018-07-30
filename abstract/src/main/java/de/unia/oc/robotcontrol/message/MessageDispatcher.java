/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.util.Dispatcher;
import de.unia.oc.robotcontrol.util.MultiRegistry;

/**
 * Dispatcher that can associates MessageTypes with Recipients.
 * @param <T>
 */
public interface MessageDispatcher<T extends Message<T>>
        extends
        MessageProcessor<T, T>,
        MultiRegistry<MessageType<T>, MessageRecipient<T>>,
        Dispatcher<T>
{
    @Override
    void dispatch(T msg) throws IllegalArgumentException;
}
