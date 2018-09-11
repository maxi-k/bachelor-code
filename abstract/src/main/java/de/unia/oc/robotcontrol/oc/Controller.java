/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.flow.FlowableTransformer;
import org.checkerframework.checker.signedness.qual.Constant;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Set;

/**
 * Implements the (Organic Computing) Controller.
 * Uses an {@link ObservationModel} to communicate with the {@link Observer}
 *
 * @param <WorldState> the type that is reported by the observer for updates
 * @param <Model> the subtype of {@link ObservationModel} used by observer and controller
 * @param <Command> the type of command this emits
 */
public interface Controller<WorldState extends Object, Model extends ObservationModel<WorldState>, Command extends Object>
        extends FlowableTransformer<WorldState, Command> {

    /**
     * @return the currently active observation model
     */
    Model getObservationModel();

    /**
     * Set the observer for this controller.
     * Is responsible for setting the observation model on
     * the given observer initially.
     *
     * @param observer the observer to use
     */
    void setObserver(Observer<?, ? extends WorldState, ? super Model> observer);

    /**
     * Generate a set of all possible actions this controller can emit.
     * @return a {@link Set} of {@link Command}
     */
    @Pure
    @Constant
    Set<? extends Command> getPossibleActions();
}
