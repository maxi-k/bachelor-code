/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CharEncoding;
import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.FixedEncoding;

public class CharIdentifier
        implements MessageIdentifier<Character> {

    private final CharEncoding idEncoding;

    public CharIdentifier(CodingContext context) {
        this.idEncoding = new CharEncoding(context);
    }

    public CharIdentifier() {
        this(CodingContext.NATIVE);
    }

    @Override
    public CodingContext getContext() {
        return idEncoding.getContext();
    }

    @Override
    public FixedEncoding<Character> getIdentifierEncoding() {
        return this.idEncoding;
    }

    @Override
    public CharIdentifier withContext(CodingContext context) {
        return new CharIdentifier(context);
    }
}
