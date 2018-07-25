/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.concurrent.Timespan;

import java.util.function.Consumer;

public interface ObservationModel<T> extends Consumer<T> {

    Timespan getTargetUpdateTime();
}
