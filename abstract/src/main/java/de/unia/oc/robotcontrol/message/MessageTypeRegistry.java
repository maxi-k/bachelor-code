/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.util.Registry;
import de.unia.oc.robotcontrol.util.Tuple;

/**
 * A generic interface for a Registry that associates a MessageIdentifier
 * with a MessageType, so that it is possible to decode received bytes
 * with an included identifier to all registered MessageTypes.
 *this.mockAnswerEncoding.encode(Arrays.asList(
                (int) (Math.random() * 100),
                (int) (Math.random() * 100),
                (int) (Math.random() * 100)
        ));
 * @param <I> The element used as an identifier
 */
public interface MessageTypeRegistry<I extends Object>
        extends Registry<I, MessageType>, Encoding<Message> {

    /**
     * @return the identifier instance used to encode instances of {@link I}
     */
    MessageIdentifier<I> getIdentifier();

    /**
     * {@inheritDoc}
     *
     * Encode the given message, using the identifier registered to
     * its type to identify the resulting bytes with the
     * {@link #getIdentifier()} encoding.
     *
     * @param object The thing encode
     * @return a byte array containing both the message data and the
     * encoded identifier associated with that messages type
     * @throws IllegalArgumentException if the underlying message encoding failed,
     * the identifier encoding failed, or the messages' {@link MessageType} is
     * not registered
     */
    @Override
    @SuppressWarnings("unchecked")
    default byte[] encode(Message object) throws IllegalArgumentException {
        return getIdentifier()
                .encode(Tuple.create(
                        getKeyFor(object.getType()).orElseThrow(IllegalArgumentException::new),
                        object.getType().encode(object))
                );
    }

    /**
     *
     * {@inheritDoc}
     *
     * Decode the given message, using the {@link #getIdentifier()} {@link MessageIdentifier}
     * to split the identifier from the actual message data, looking up the
     * {@link MessageType} of that message in the registry, and using that
     * to decode the actual message.
     *
     * @param raw a byte representation of the resulting message and
     *            the identifier used to identify its {@link MessageType}
     *            from just the bytes.
     * @return the message that was encoded in the given byte array along
     * with its identifier.
     * @throws IllegalArgumentException if the underlying message encoding failed,
     * the identifier encoding failed, or the messages' {@link MessageType} is
     * not registered
     */
    @Override
    default Message decode(byte[] raw) throws IllegalArgumentException {
        return getIdentifier()
                .decode(raw)
                .joinWith((I id, byte[] rest) ->
                        getValueFor(id)
                                .orElseThrow(IllegalArgumentException::new)
                                .decode(rest));
    }
}
