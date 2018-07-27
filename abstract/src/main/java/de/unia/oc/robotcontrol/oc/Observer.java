/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.flow.old.InFlowElement;
import de.unia.oc.robotcontrol.flow.old.OutFlowElement;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Observer<@NonNull T, M extends @NonNull ObservationModel<T>>
        extends InFlowElement, OutFlowElement {

    M getObservationModel();

    void setObservationModel(M observationModel);

    @NonNull T getModelState();
}
