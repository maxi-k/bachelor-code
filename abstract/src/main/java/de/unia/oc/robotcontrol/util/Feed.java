/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.util.Observable;

public interface Feed<T> {

    /**
     *
     * @return An Observable that can emit data from this source
     */
    Observable asObservable();
}
