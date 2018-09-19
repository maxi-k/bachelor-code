/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.CodingUtil;
import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.coding.FixedEncoding;
import de.unia.oc.robotcontrol.util.Tuple;

/**
 * A message identifier that can be encoded, which makes it
 * possible to determine the type of message from just bytes.
 *
 * @param <T> The type of object that is encoded and used for identification
 */
public interface MessageIdentifier<T>
        extends Encoding<Tuple<T, byte[]>> {

    /**
     * @return The Encoding for the identifier type
     */
    FixedEncoding<T> getIdentifierEncoding();

    /**
     * {@inheritDoc}
     *
     * Encode the given identifier instance and
     * add it to the bytes of the passed objects,
     * resulting in bytes which can be identified
     * using the passed message identifier,
     * and decoded using {@link #decode(byte[])}
     *
     * @param object The thing to transform / encode
     * @return a new byte array
     * @throws IllegalArgumentException
     */
    @Override
    default byte[] encode(Tuple<T, byte[]> object) throws IllegalArgumentException {
        FixedEncoding<T> e = getIdentifierEncoding();
        Tuple<byte[], byte[]> encoded = object.mapFirst(e::encode);
        return CodingUtil.join(encoded.first, encoded.second);
    }

    /**
     * {@inheritDoc}
     *
     * Split the identifier bytes from the actual message data bytes,
     * returning tuple of the already decoded identifier and
     * the rest of the bytes.
     *
     * @param raw a byte representation of the value type coded by
     *            this instance of {@link Encoding}
     * @return a new tuple containing the decoded identifier and
     * the message bytes
     * @throws IllegalArgumentException
     */
    @Override
    default Tuple<T, byte[]> decode(byte[] raw) throws IllegalArgumentException {
        FixedEncoding<T> e = getIdentifierEncoding();
        Tuple<byte[], byte[]> split = CodingUtil.splitAt(raw, e.numBytes());
        return split.mapFirst(e::decode);
    }

    /**
     * {@inheritDoc}
     *
     * @param context The coding context that has to be set
     * @return A new instance of {@link MessageIdentifier} with
     * its context set to the given context.
     */
    @Override
    default MessageIdentifier<T> withContext(CodingContext context) {
        MessageIdentifier<T> self = this;
        return new MessageIdentifier<T>() {
            @Override
            public FixedEncoding<T> getIdentifierEncoding() {
                return self.getIdentifierEncoding();
            }

            @Override
            public CodingContext getContext() {
                return context;
            }
        };
    }
}
