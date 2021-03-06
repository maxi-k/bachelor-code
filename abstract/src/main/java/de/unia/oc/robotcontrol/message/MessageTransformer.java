/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.FlowableTransformer;

/**
 * Generic interface for some element that can process Messages,
 * that is, it can both send and receive them.
 * @param <Input> The subclass of messages this can accept
 * @param <Output> The subclass of messages this can send
 */
public interface MessageTransformer<Input extends Message, Output extends Message>
        extends FlowableTransformer<Input, Output> {
}
