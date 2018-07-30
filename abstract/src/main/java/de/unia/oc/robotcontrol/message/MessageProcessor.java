/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.ProcessorProvider;

/**
 * Generic interface for some element that can process Messages,
 * that is, it can both send and receive them.
 * @param <Input> The subclass of messages this can accept
 * @param <Output> The subclass of messages this can send
 */
public interface MessageProcessor<Input extends Message<Input>, Output extends Message<Output>>
        extends ProcessorProvider<Input, Output> {
}
