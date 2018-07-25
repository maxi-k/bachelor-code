/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.flow.InFlowElement;
import de.unia.oc.robotcontrol.flow.OutFlowElement;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Constant;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Set;

public interface Controller<T, M extends ObservationModel<T>, O> extends InFlowElement, OutFlowElement {

    void setObserver(Observer<T, M> observer);

    @Pure
    @Constant
    Set<@NonNull O> getPossibleActions();
}
