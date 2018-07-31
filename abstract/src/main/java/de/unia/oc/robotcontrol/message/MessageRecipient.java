/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.FlowableSink;

/**
 * Generic interface for some element that can receive Messages.
 * For elements that both send and receive messages, it is recommended
 * to use the {@link MessageTransformer} interface instead.
 * @param <T> The (sub)type of messages this can receive
 */
public interface MessageRecipient<T extends Message<T>> extends FlowableSink<T> {

}
