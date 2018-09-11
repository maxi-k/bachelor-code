/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import java.time.Duration;

/**
 * The observation-model is set on the observer by the controller,
 * and is a way for the controller to tweak some aspects of the observer.
 *
 * The parameters which can be set are very domain-specific, and as such
 * there are none defined in this super-interface, except
 * for {@link #getTargetUpdateTime()}, which reflects the update frequency
 * the controller wants from sensors et al.
 *
 * This should probably be implemented immutably to avoid concurrency issues.
 * Newly generated versions can be set by the controller using
 * {@link Observer#setObservationModel(ObservationModel)}.
 *
 * @param <WorldState> the type of state the controller and observer are working with
 */
public interface ObservationModel<WorldState> {

    /**
     * The desired update frequency from the controller.
     *
     * @return a {@link Duration} instance
     */
    Duration getTargetUpdateTime();
}
