/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

public interface ValueBoundMulticast<Topic extends Object, Value extends Object>
    extends FlowableMulticast<Topic, Value> {

    Topic topicFromValue(Value value);

}
