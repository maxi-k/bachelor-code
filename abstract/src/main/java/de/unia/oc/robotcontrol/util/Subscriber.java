/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.util.Observer;

public interface Subscriber<T> {

    /**
     *
     * @return An observer that can be notified of data for
     * this data sink.
     */
    Observer asObserver();
}
