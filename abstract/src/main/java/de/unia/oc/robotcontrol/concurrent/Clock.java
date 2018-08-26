/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.reactivestreams.Publisher;

import java.time.Duration;

/**
 * An instance of {@link TimeProvider} that explicitly mutable,
 * that is, the interval at which ticks are emitted on the Publisher
 * from {@link #getTicks()} can be set using {@link #setInterval(Duration)}
 */
public interface Clock extends TimeProvider {

    /**
     * Set the interval at which ticks are emitted on {@link #getTicks()}.
     *
     * This does not make guarantees as to when the Publisher will switch
     * to the new interval, only that it will use that interval eventually.
     *
     * @param interval the interval to eventually swich to
     */
    void setInterval(Duration interval);

    /**
     * @return the current interval at which values are emitted on the
     * {@link Publisher} retrievable using {@link #getTicks()}
     */
    Duration getCurrentInterval();
}
