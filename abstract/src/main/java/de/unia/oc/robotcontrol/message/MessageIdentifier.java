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

    @Override
    default byte[] encode(Tuple<T, byte[]> object) throws IllegalArgumentException {
        FixedEncoding<T> e = getIdentifierEncoding();
        Tuple<byte[], byte[]> encoded = object.mapFirst(e::encode);
        return CodingUtil.join(encoded.first, encoded.second);
    }

    @Override
    default Tuple<T, byte[]> decode(byte[] raw) throws IllegalArgumentException {
        FixedEncoding<T> e = getIdentifierEncoding();
        Tuple<byte[], byte[]> split = CodingUtil.splitAt(raw, e.numBytes());
        return split.mapFirst(e::decode);
    }

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
