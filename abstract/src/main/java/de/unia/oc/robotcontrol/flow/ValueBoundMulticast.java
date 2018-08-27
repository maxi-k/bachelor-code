/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

/**
 * A special multicast that can derive the {@link Topic} on which a
 * message {@link Value} is to be multicast on from the value itself.
 *
 * @param <Topic> the type of topic this will dispatch on
 * @param <Value> the type of value this will receive and emit
 */
public interface ValueBoundMulticast<Topic extends Object, Value extends Object>
    extends FlowableMulticast<Topic, Value> {

    Topic topicFromValue(Value value);

}
