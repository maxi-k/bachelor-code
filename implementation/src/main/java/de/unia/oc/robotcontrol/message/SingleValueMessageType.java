package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.Encoding;

/**
 * Implementation of {@link MessageType} for a {@link SingleValueMessage}.
 * @param <T> the type of the value the {@link SingleValueMessage} wraps
 */
public class SingleValueMessageType<T> implements MessageType<SingleValueMessage<T>> {

    /**
     * The encoding used to encode and decode the value held by the message
     */
    private final Encoding<T> encoding;

    /**
     * Creates a new instance of {@link SingleValueMessageType} using
     * the given encoding.
     * @param encoding the encoding to use to encode and decode
     *                 the value held by message the message.
     */
    public SingleValueMessageType(Encoding<T> encoding) {
        this.encoding = encoding;
    }

    /**
     * {@inheritDoc}
     *
     * Encodes the given message by encoding the value ({@link SingleValueMessage#getValue()}.
     *
     * @param object the message to encode
     * @return a byte array representing the message (= the wrapped value)
     * @throws IllegalArgumentException if {@link #encoding} throws on the wrapped value
     */
    @Override
    public byte[] encode(SingleValueMessage<T> object) throws IllegalArgumentException {
        return encoding.encode(object.getValue());
    }

    /**
     * {@inheritDoc}
     *
     * Decodes a {@link SingleValueMessage} by decoding the value using
     *
     * {@link #encoding}, and wrapping it with {@link SingleValueMessage#SingleValueMessage(Object)}
     * @param raw the bytes to decode
     * @return a new instance of {@link SingleValueMessage}
     * @throws IllegalArgumentException if {@link #encoding} throws on the bytes
     */
    @Override
    public SingleValueMessage<T> decode(byte[] raw) throws IllegalArgumentException {
        return produce(encoding.decode(raw));
    }

    /**
     * Create a new instance of {@link SingleValueMessage} with
     * the {@link MessageType} set to {@code this} from the
     * given value.
     * @param value The value to wrap in a {@link SingleValueMessage}
     * @return a new instance of {@link SingleValueMessage} wrapping the given value.
     */
    public SingleValueMessage<T> produce(T value) {
        long now = System.currentTimeMillis();
        return new SingleValueMessage<T>(value) {
            @Override
            public MessageType<SingleValueMessage<T>> getType() {
                return SingleValueMessageType.this;
            }

            @Override
            public long getCreationTime() {
                return now;
            }
        };
    }

    @Override
    public CodingContext getContext() {
        return encoding.getContext();
    }
}
