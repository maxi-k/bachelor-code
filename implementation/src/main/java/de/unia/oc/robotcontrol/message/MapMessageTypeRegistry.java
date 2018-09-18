/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.util.BidirectionalRegistry;

/**
 * Implementation of {@link MessageTypeRegistry} which uses
 * the {@link BidirectionalRegistry} utility class to store values.
 *
 * Use one of the Methods in {@link Messaging} to create instances of this
 * from outside of the package.
 * @param <I> the type of value used for the {@link MessageIdentifier}
 */
class MapMessageTypeRegistry<I extends Object>
        extends BidirectionalRegistry<I, MessageType>
        implements MessageTypeRegistry<I> {

    /**
     * The Encoding used to separate the identifier
     * from the actual message data.
     */
    private MessageIdentifier<I> identifier;

    MapMessageTypeRegistry(MessageIdentifier<I> identifier) {
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

    /**
     * {@inheritDoc}
     *
     * Implemented non-immutably (mutates this object), as
     * the registry would have to be copied completely.
     * Sets the context of the identifier.
     *
     * @param context The coding context that has to be set
     * @return this {@link MapMessageTypeRegistry}
     */
    @Override
    public synchronized MapMessageTypeRegistry<I> withContext(CodingContext context) {
        this.identifier = this.identifier.withContext(context);
        return this;
    }
}
