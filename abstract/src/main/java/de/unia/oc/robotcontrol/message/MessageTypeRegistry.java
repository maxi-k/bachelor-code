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

    @Override
    @SuppressWarnings("unchecked")
    default byte[] encode(Message object) throws IllegalArgumentException {
        return getIdentifier()
                .encode(Tuple.create(
                        getKeyFor(object.getType()).orElseThrow(IllegalArgumentException::new),
                        object.getType().encode(object))
                );
    }

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
