/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

public interface FlowProvider<Input, Output> extends PublisherProvider<Output>, SubscriberProvider<Input> {
}
