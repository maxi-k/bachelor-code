/* 2016 */
package de.unia.oc.robotcontrol.message;

public interface MessageDispatcher<T extends Message>
        extends MessageSender<T>, MessageRecipient<T> {

    <M extends Message> void registerRecipientForType(
            MessageRecipient<M> recipient,
            MessageType<M> type);

    <M extends Message> boolean removeRecipientForType(
            MessageRecipient<M> recipient,
            MessageType<M> type);

    <M extends Message> void dispatchMessage(Message<M> m);

}
