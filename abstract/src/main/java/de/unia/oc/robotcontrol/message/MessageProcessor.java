/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.FlowableProcessor;

/**
 * Message-Specific {@link FlowableProcessor} interface,
 * used for making the implementation of message-specific
 * processor instances more intuitive.
 *
 * @param <Input> the type of message this can accept
 * @param <Output> the type of message this will output
 */
public interface MessageProcessor<Input extends Message, Output extends Message>
    extends
        MessageTransformer<Input, Output>,
        FlowableProcessor<Input, Output> {
}
