/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.flow.FlowableTransformer;
import de.unia.oc.robotcontrol.message.Message;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Observer<@NonNull WorldState, Model extends @NonNull ObservationModel<WorldState>>
        extends FlowableTransformer<Message, WorldState> {

    Model getObservationModel();

    void setObservationModel(Model observationModel);

    @NonNull WorldState getModelState();
}
