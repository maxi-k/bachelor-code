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

    /**
     * Derives a topic from the given value, making it possible
     * to create a multicast function based on the value alone,
     * without specifying a topic.
     *
     * @param value a value that a topic can be derived from
     * @return the topic associated with the value
     */
    Topic topicFromValue(Value value);

}
