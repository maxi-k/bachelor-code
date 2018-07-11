/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.Encoding;

/**
 * An interface that defines the type of a message by
 * providing an {@link Encoding} for it.
 *
 * @param <T>
 */
public interface MessageType<T extends Message> extends Encoding<T> {

    @Override
    T decode(byte[] raw) throws IllegalArgumentException;

    @Override
    byte[] encode(T object) throws IllegalArgumentException;

    /**
     * Try to cast the message to a message of this message type.
     * @param m
     * @return A Message of type {@link T}
     * @throws IllegalArgumentException if the message could not be cast.
     */
    @SuppressWarnings("unchecked")
    default T cast(Message m) throws IllegalArgumentException {
        MessageType t = m.getType();
        if (t != this) {
            throw new IllegalArgumentException("MessageType Casting: The MessageType of the Message type did not match");
        }
        try {
            return (T) m;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("MessageType Casting: Even though the MessageType matched, the Message could not be cast.");
        }
    }

    default Encoding<Message> asEncoding() {
        Encoding<T> self = this;
        return new Encoding<Message>() {

            @Override
            public CodingContext getContext() {
                return self.getContext();
            }

            @Override
            @SuppressWarnings("unchecked")
            public byte[] encode(Message object) throws IllegalArgumentException {
                try {
                    return self.encode((T) object);
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Argument was of wrong class");
                }
            }

            @Override
            public Message decode(byte[] raw) throws IllegalArgumentException {
                return self.decode(raw);
            }
        };
    }

    static <T extends Message> MessageType<T> fromEncoding(Encoding<T> e) {
        return new MessageType<T>() {
            @Override
            public byte[] encode(T object) throws IllegalArgumentException {
                return e.encode(object);
            }

            @Override
            public T decode(byte[] raw) throws IllegalArgumentException {
                return e.decode(raw);
            }

            @Override
            public CodingContext getContext() {
                return e.getContext();
            }
        };
    }
}
