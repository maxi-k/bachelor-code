/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import java.time.Duration;

public interface Clock extends TimeProvider {

    void setInterval(Duration interval);
}
