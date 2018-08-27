/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import java.time.Duration;

public interface ObservationModel<WorldState> {

    Duration getTargetUpdateTime();
}
