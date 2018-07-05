/* 2016 */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.Contextual;
import de.unia.oc.robotcontrol.util.BidirectionalRegistry;

public class CharMessageTypeRegistry
        extends BidirectionalRegistry<Character, MessageType>
        implements MessageTypeRegistry<Character>, Contextual {

    private CharIdentifier identifier;

    private CharMessageTypeRegistry() {
        super();
        this.identifier = new CharIdentifier();
    }

    @Override
    public MessageIdentifier<Character> getIdentifier() {
        return identifier;
    }

    @Override
    public CodingContext getContext() {
        return this.identifier.getContext();
    }

    @Override
    public CharMessageTypeRegistry withContext(CodingContext context) {
        this.identifier = this.identifier.withContext(context);
        return this;
    }
}
