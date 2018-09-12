/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.reactivestreams.Publisher;

import java.time.Duration;

/**
 * Abstract interface for something that provides time by 'ticking',
 * using a reactive-streams {@link Publisher} to do.
 *
 * The Publisher will emit increasing longs at some - possibly changing - rate.
 */
public interface TimeProvider {

    /**
     * Retrieve an instance of {@link Publisher} with the {@code Long}
     * type parameter, that will emit increasing Longs at some rate
     * (which may change).
     *
     * It is guaranteed that this Publisher can be subscribed to,
     * and that it will never complete or error.
     *
     * @return An instance of {@link Publisher}
     */
    Publisher<Long> getTicks();

}
