/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.flow.FlowableTransformer;
import de.unia.oc.robotcontrol.message.Message;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Observer<@NonNull T, M extends @NonNull ObservationModel<T>>
        extends FlowableTransformer<Message, T> {

    M getObservationModel();

    void setObservationModel(M observationModel);

    @NonNull T getModelState();
}
