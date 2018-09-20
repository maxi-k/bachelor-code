/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.unia.oc.robotcontrol.util.Tuple;
import de.unia.oc.robotcontrol.visualization.Metrics;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.signedness.qual.Constant;
import org.checkerframework.dataflow.qual.Pure;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Implementation of {@link Controller} using {@link TickingController} as a basis,
 * using Q-Learning to make decisions and learn.
 *
 * This implementation uses a Guava {@link HashBasedTable} to store the Q-Matrix.
 *
 * @param <WorldState> the type of the report received from the observer
 * @param <Model> the type of the observation model used
 * @param <Command> the type of the command emitted by this controller
 */
public abstract class QLearningController<WorldState extends Object,
        Model extends ObservationModel<WorldState>,
        Command extends Object>
        extends TickingController<WorldState, Model, Command> {

    /**
     * The Q-Matrix table, which is update by {@link #applyWorldUpdate(Object)}
     */
    private final Table<String, Command, Double> qMatrix;
    /**
     * The list of possible actions this can emit
     */
    private final List<? extends Command> possibleActions;
    /**
     * A Consumer linking back to a {@link de.unia.oc.robotcontrol.visualization.RuntimeMetrics},
     * for reporting runtime metrics.
     */
    private final Consumer<Double> matrixSizeMetrics;

    /**
     * The last action that was chosen by the Q-Matrix.
     * Set by {@link #applyWorldUpdate(Object)}
     */
    private volatile @MonotonicNonNull Command lastAction;
    /**
     * The last world state that was received from the observer.
     * Set by {@link #applyWorldUpdate(Object)}
     */
    private volatile @MonotonicNonNull WorldState lastWorldState;

    @SuppressWarnings("initialization")
    public QLearningController() {
        super();
        this.qMatrix = HashBasedTable.create(getApproximateStateSpaceSize(), getPossibleActions().size()) ;
        this.possibleActions = new ArrayList<>((Collection<? extends Command>) getPossibleActions());
        this.matrixSizeMetrics = Metrics.instance().registerCallback("Controller QMatrix Size");
    }

    /**
     * Apply the {@link WorldState} to the Q-Matrix using the well-known algorithm,
     * getting the reward from the {@link #getRewardFor(Object, Object)} function.
     * @param worldState the {@link WorldState} to apply to the matrix.
     */
    @RequiresNonNull("this.qMatrix")
    @EnsuresNonNull({"this.lastWorldState", "this.lastAction"})
    protected synchronized void applyWorldUpdate(@NonNull WorldState worldState) {
        String encoded = encodeState(worldState);
        Tuple<@NonNull Command, @NonNull Double> bestAction = getBestActionFor(qMatrix.row(encoded));

        if (lastAction != null && lastWorldState != null) {
            String lastStateEncoded = encodeState(lastWorldState);

            double prevQ = getRewardOrDefault(lastWorldState, lastAction);
            double reward = getRewardFor(worldState, bestAction.first);

            qMatrix.put(lastStateEncoded, lastAction,
                    prevQ + getSpeedFactor() * (reward + getDiscountFactor() * bestAction.second - prevQ));

            matrixSizeMetrics.accept((double) this.qMatrix.size());
        }

        this.lastWorldState = worldState;
        this.lastAction = bestAction.first;
    }

    /**
     * Choose action to actually execute. This is usually the action which the
     * controller thinks will yield the highest reward, but may randomly
     * be a different one based on the {@link #getExploreFactor()} factor.
     * @return the chosen {@link Command} instance.
     */
    protected synchronized Command chooseAction() {
        if (lastWorldState == null || Math.random() < getExploreFactor()) {
            return chooseRandomAction();
        }
        return getBestActionFor(qMatrix.row(encodeState(lastWorldState))).first;
    }

    /**
     * {@inheritDoc}
     *
     * First applies the update to the Q-Matrix based on the passed {@link WorldState},
     * then chooses an action using {@link #chooseAction()}.
     *
     * @param state the state that was received.
     * @return the command to publish
     */
    @Override
    protected Command tick(WorldState state) {
        applyWorldUpdate(state);
        return chooseAction();
    }

    /**
     * Choose the best action based on a row out of the Q-Matrix.
     * @param actionMap the row out of the Q-Matrix associated with a certain {@link WorldState}
     * @return A tuple (Command, Reward) containing the chosen action and
     * the expected reward.
     */
    @SuppressWarnings("nullness")
    protected Tuple<@NonNull Command, @NonNull Double> getBestActionFor(Map<Command, Double> actionMap) {
        if (actionMap.isEmpty()) return Tuple.create(chooseRandomAction(), getDefaultReward());
        double bestVal = - Double.MAX_VALUE;
        @MonotonicNonNull Command bestAction = null;

        for (Map.Entry<Command, Double> action : actionMap.entrySet()) {
            if (action.getValue() > bestVal) {
                bestVal = action.getValue();
                bestAction = action.getKey();
            }
        }

        return bestAction == null
                ? Tuple.create(chooseRandomAction(), getDefaultReward())
                : Tuple.create(bestAction, actionMap.get(bestAction));
    }

    /**
     * @return a random {@link Command} instance out of {@link #getPossibleActions()}
     */
    protected @NonNull Command chooseRandomAction() {
        synchronized (this.possibleActions) {
            return this.possibleActions.get((int) (Math.random() * this.possibleActions.size()));
        }
    }

    /**
     * Return the reward for the given state and action as predicted by
     * the Q-Matrix, or the {@link #getDefaultReward()} if there is no
     * prediction yet.
     *
     * @param worldState the state to predict the reward for
     * @param action the action to predict the reward for
     * @return the expected reward, as predicted by the Q-Matrix
     */
    private double getRewardOrDefault(@Nullable WorldState worldState, @Nullable Command action) {
        if (worldState == null || action == null) return getDefaultReward();
        synchronized (this.qMatrix) {
            Double val = qMatrix.get(encodeState(worldState), action);
            return val == null ? getDefaultReward() : val;
        }
    }

    /**
     * Encode the given {@link WorldState} instance as a string, which should
     * be different for different {@link WorldState}s which differ in the
     * data encoded in them
     * @param state the state to encode
     * @return a String representation of the given state
     */
    @Pure
    protected abstract String encodeState(@NonNull WorldState state);

    /**
     * Approximate the size of the possible states, which is used to initialize the
     * {@link #qMatrix} so that there are not as many inefficient enlargements in
     * the beginning (when many new states are recorded)
     * @return an estimate of the amount of states put into the matrix
     */
    @Pure
    @Constant
    protected abstract int getApproximateStateSpaceSize();

    /**
     * The reward function, which gives a reward for the action taken
     * by the controller based on the state.
     * @param state the state the command was associated with
     * @param action the action the controller chose
     * @return the reward that should be used to update the Q-Matrix with
     */
    @Pure
    protected abstract double getRewardFor(WorldState state, Command action);

    /**
     * Return the scheduler that should be used to run this controller on
     * @return an Instance of {@link Scheduler}
     */
    protected Scheduler createScheduler() {
        return Schedulers.newSingle("Controller");
    }

    /**
     * @return the default reward to be put into the Q-Matrix
     * when there is not entry yet.
     */
    @Pure
    protected @NonNull double getDefaultReward() {
        return 0;
    }

    /**
     * @return the 'learning-speed' factor from the Q-Learning
     * matrix update formula
     */
    @Pure
    protected double getSpeedFactor() {
        return 0.1;
    }

    /**
     * @return the 'discount' factor from the Q-Learning
     * matrix update formula
     */
    @Pure
    protected double getDiscountFactor() {
        return 0.4;
    }

    /**
     * @return the 'explore' factor from the Q-Learning
     * matrix update formula
     */
    @Pure
    protected double getExploreFactor() {
        return 0.2;
    }
}
