/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.concurrent.TimeProvider;
import de.unia.oc.robotcontrol.flow.FlowableTransformer;
import de.unia.oc.robotcontrol.flow.SelectiveRecipient;
import de.unia.oc.robotcontrol.message.Message;

public interface Observer<Incoming extends Message, WorldState extends Object,
        Model extends ObservationModel<WorldState>>
        extends FlowableTransformer<Incoming, WorldState>, SelectiveRecipient<Incoming> {

    Model getObservationModel();

    void setObservationModel(Model observationModel);

    WorldState getModelState();

    TimeProvider getTimeProvider();
}
