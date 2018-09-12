/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.Encoding;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An interface that defines the type of a message by
 * providing an {@link Encoding} for it.
 *
 * @param <T> the type of message this acts as Message type for,
 *            and can encode and decode
 */
public interface MessageType<T extends Message> extends Encoding<T> {

    @Override
    @NonNull T decode(byte[] raw) throws IllegalArgumentException;

    @Override
    byte[] encode(T object) throws IllegalArgumentException;

    /**
     * Try to cast the message to a message of this message type.
     *
     * @param m the message to try to cast to {@link T}
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

    /**
     * @return An instance of Encoding with the generic 'Message' type parameter
     */
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

            @Override
            public String toString() {
                return "[Encoding from MessageType: " + self.toString();
            }
        };
    }

    default MessageType<Message> asSimpleType() {
        MessageType<T> self = this;
       return new MessageType<Message>() {
           @Override
           public Message decode(byte[] raw) throws IllegalArgumentException {
               return self.decode(raw);
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
           public CodingContext getContext() {
               return self.getContext();
           }

           @Override
           public String toString() {
               return self.toString();
           }
       };
    }

    /**
     * Construct a {@link MessageType} instance from an existing encoding
     *
     * @param e The encoding which is used for this {@link MessageType}
     * @param <T> The type of message this encodes
     * @return An instance of {@link MessageType} that uses the given encoding
     */
    static <T extends Message<T>> MessageType<T> fromEncoding(Encoding<T> e) {
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

            @Override
            public String toString() {
                return "[MessageType " + super.toString() + " for Encoding: " + e.toString() + "]";
            }
        };
    }

    default MessageType<T> withName(String name) {
        MessageType<T> self = this;

        return new MessageType<T>() {
            @NonNull
            @Override
            public T decode(byte[] raw) throws IllegalArgumentException {
                return self.decode(raw);
            }

            @Override
            public byte[] encode(T object) throws IllegalArgumentException {
                return self.encode(object);
            }

            @Override
            public CodingContext getContext() {
                return self.getContext();
            }

            @Override
            public String toString() {
                return "[MessageType " + self.toString() + " - " +  name + "]";
            }
        };
    }
}
