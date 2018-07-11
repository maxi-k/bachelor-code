/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.FixedEncoding;

public class SimpleMessageIdentifier<T> implements MessageIdentifier<T> {

    private final FixedEncoding<T> idEncoding;

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
