/* 2016 */
package de.unia.oc.robotcontrol.data;

/**
 * A generic interface that wraps some data value,
 * presumably received from a sensor or to be sent to an
 * actuator.
 * As this is probably passed between threads, it should be
 * implemented immutably.
 *
 * @param <T> The type of the data
 */
public interface DataPayload<T> {

    /**
     * Returns the creation time of the wrapped data as
     * a timestamp (seconds since 1.1.1970)
     *
     * @return The creation timestamp of this data payload in seconds
     */
    long getCreationTime();

    /**
     *
     * @return The data wrapped by this payload
     */
    T getData();
}
