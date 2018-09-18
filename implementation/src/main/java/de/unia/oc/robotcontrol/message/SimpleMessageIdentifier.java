/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.FixedEncoding;

/**
 * A simple message identifier wrapping an existing {@link FixedEncoding}.
 * @param <T> the type of value the existing encoding encodes.
 */
public class SimpleMessageIdentifier<T> implements MessageIdentifier<T> {

    /**
     * The existing {@link FixedEncoding} used to encode the identifier
     */
    private final FixedEncoding<T> idEncoding;

    /**
     * Creates a new instance of {@link SimpleMessageIdentifier}
     * using the given encoding to encode and decode the identifier
     * value.
     * @param encoding the encoding to use
     */
    public SimpleMessageIdentifier(FixedEncoding<T> encoding) {
        this.idEncoding = encoding;
    }

    @Override
    public CodingContext getContext() {
        return idEncoding.getContext();
    }

    @Override
    public FixedEncoding<T> getIdentifierEncoding() {
        return this.idEncoding;
    }
}
