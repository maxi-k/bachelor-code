/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

public enum FlowStrategy {

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
	 * Buffer messages for which the recipient is not ready. Once the recipient is ready,
     * pass the buffered messages to it one by one, appending new messages to the buffer
     * while it is not empty.
     *
     * To always pass the List of buffered messages as a whole, use {@link #REDUCE} with
     * reducer (list[n], new) -> list[n + 1] instead.
     *
     * There should always be a maximum buffer size. To handle overflow,
     * the buffer itself should also define a {@link FlowStrategy},
     * which kicks in when it is full.
     *
     * If it does not define a maximum buffer size, there might be a {@link OutOfMemoryError}.
	 */
	BUFFER,

    /**
     * Reduce the received messages to a single value while the recipient is not ready.
     * Once it is, pass the reduced value to the recipient. Reduction is done with the
     * pattern known from reduce / fold functions, that is (reduced, new) -> reduced.
     */
    REDUCE;
}
