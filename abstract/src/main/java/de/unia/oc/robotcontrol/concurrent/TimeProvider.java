/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.reactivestreams.Publisher;

import java.time.Duration;

public interface TimeProvider {

    Publisher<Long> getTicks();

    Duration getCurrentInterval();

}
