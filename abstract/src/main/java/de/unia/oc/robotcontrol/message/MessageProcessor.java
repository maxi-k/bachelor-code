/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.FlowableProcessor;

public interface MessageProcessor<Input extends Message, Output extends Message>
    extends
        MessageTransformer<Input, Output>,
        FlowableProcessor<Input, Output> {
}
