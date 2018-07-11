/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.util.Registry;
import de.unia.oc.robotcontrol.util.Tuple;

public interface MessageTypeRegistry<I>
        extends Registry<I, MessageType>, Encoding<Message> {

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
