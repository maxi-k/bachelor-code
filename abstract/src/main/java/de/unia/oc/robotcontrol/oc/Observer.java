/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.concurrent.TimeProvider;
import de.unia.oc.robotcontrol.flow.FlowableTransformer;
import de.unia.oc.robotcontrol.flow.SelectiveRecipient;
import de.unia.oc.robotcontrol.message.Message;

/**
 * Implements the (Organic Computing) Observer.
 * Can use an {@link ObservationModel}, presumably passed from the {@link Controller}.
 *
 * Is also responsible for creating a {@link TimeProvider}, which reflects the
 * target update time set by the {@link ObservationModel#getTargetUpdateTime()}.
 *
 * @param <Incoming> the subtype of incoming messages this accepts
 * @param <WorldState> the type of object this reports to the controller
 * @param <Model> the subytpe of {@link ObservationModel} this is used with
 */
public interface Observer<Incoming extends Message, WorldState extends Object,
        Model extends ObservationModel<WorldState>>
        extends FlowableTransformer<Incoming, WorldState>, SelectiveRecipient<Incoming> {

    /**
     * @return the current instance of {@link Model} this is working with
     */
    Model getObservationModel();

    /**
     * Set the observation model.
     * Is also responsible for setting the internal state accordingly,
     * such as updating the {@link TimeProvider} returned by {@link #getTimeProvider()}.
     *
     * @param observationModel the observation model to use from now on
     */
    void setObservationModel(Model observationModel);

    /**
     *
     * @return the current world-state which is reported to the {@link Controller}
     */
    WorldState getModelState();

    /**
     * Define a {@link TimeProvider} which reflects the target update time
     * set by {@link ObservationModel#getTargetUpdateTime()}, and is kept up
     * to date (using an instance of {@link de.unia.oc.robotcontrol.concurrent.Clock} for example).
     * This is used on the "input"/"sensor" side of the system to clock sensors which can be clocked.
     *
     * @return an instance of {@link TimeProvider}
     */
    TimeProvider getTimeProvider();
}
