/* 2016 */
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
}
