/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.FlowableSource;

/**
 * Generic interface for some element that can send Messages.
 * For elements that both send and receive messages, it is recommended
 * to use the {@link MessageTransformer} interface instead.
 * @param <T> The (sub)type of Messages this sends
 */
public interface MessageSender<T extends Message<T>> extends FlowableSource<T> {

}
