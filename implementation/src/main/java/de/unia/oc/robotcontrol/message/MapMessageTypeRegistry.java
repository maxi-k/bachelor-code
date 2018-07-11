/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.util.BidirectionalRegistry;

class MapMessageTypeRegistry<I>
        extends BidirectionalRegistry<I, MessageType>
        implements MessageTypeRegistry<I> {

    private MessageIdentifier<I> identifier;

    public MapMessageTypeRegistry(MessageIdentifier<I> identifier) {
        this.identifier = identifier;
    }

    @Override
    public MessageIdentifier<I> getIdentifier() {
        return identifier;
    }

    @Override
    public CodingContext getContext() {
        return this.identifier.getContext();
    }

    @Override
    public synchronized MapMessageTypeRegistry<I> withContext(CodingContext context) {
        this.identifier = this.identifier.withContext(context);
        return this;
    }
}
