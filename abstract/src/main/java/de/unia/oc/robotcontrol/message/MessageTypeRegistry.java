/* 2016 */
package de.unia.oc.robotcontrol.message;

public interface MessageTypeRegistry<I> {

    MessageIdentifier<I> getIdentifier();

    <T extends Message> MessageType<T> getMessageTypeFor(I identifier);

    <T extends Message> I getIdentifierFor(MessageType<T> t);
}
