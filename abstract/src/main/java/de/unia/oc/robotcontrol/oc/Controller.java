/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.flow.FlowableTransformer;
import org.checkerframework.checker.signedness.qual.Constant;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Set;

public interface Controller<WorldState extends Object, Model extends ObservationModel<WorldState>, Command extends Object>
        extends FlowableTransformer<WorldState, Command> {

    Model getObservationModel();

    void setObserver(Observer<?, ? extends WorldState, ? super Model> observer);

    @Pure
    @Constant
    Set<? extends Command> getPossibleActions();
}
