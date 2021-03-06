/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

/**
 * Categorization of the possible effects a {@link Flowable} flow element
 * might have on the overall program flow (pipeline),
 * especially concerning situations where a recipient cannot handle the
 * rate of data coming from a source (backpressure).
 *
 * Inspired by the backpressure types as defined by the Reactor Framework.
 */
public enum FlowStrategyType {

    /**
     * When the recipient is not able to receive a message, don't send it.
     */
    IGNORE,

    /**
     * Throw an exception when a recipient can't receive a message.
     */
    ERROR,

    /**
     * Hold the first message that could not be sent to the recipient successfully.
     * Drop new messages. Once the recipient is ready, pass the held message and all
     * subsequent messages in the same manner.
     */
    OLDEST,

    /**
     * Hold the first message that could not be sent to the recipient successfully.
     * When a message comes in, replace the held message with it. Once the recipient
     * is ready, pass the held message and all subsequent messages in the same manner.
     */
    LATEST,

    /**
     *
     * Buffer messages for which the recipient is not ready. Once the recipient is ready,
     * pass the buffered messages to it one by one, appending new messages to the buffer
     * while it is not empty.
     *
     * There should always be a maximum buffer size.
     *
     * If it does not define a maximum buffer size, there might be a {@link OutOfMemoryError}.
     */
    BUFFER,

    /**
     * Reduce the received messages to a single value while the recipient is not ready.
     * Once it is, pass the reduced value to the recipient. Reduction is done with the
     * pattern known from reduce / fold functions, that is {@code (reduced, new) -> reduced}.
     *
     * While the recipient is ready to receive, pass values through using the same reducing function.
     */
    REDUCE,

    /**
     * Do not change the Flow Strategy already existing on the Publisher.
     */
    TRANSPARENT,

    /**
     * Any flow strategy that is not expressible using these types.
     */
    UNDEFINED;
}
