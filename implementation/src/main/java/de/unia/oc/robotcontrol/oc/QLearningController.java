/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.unia.oc.robotcontrol.util.Tuple;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.signedness.qual.Constant;
import org.checkerframework.dataflow.qual.Pure;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class QLearningController<WorldState extends Object,
        Model extends ObservationModel<WorldState>,
        Command extends Object>
        extends TickingController<WorldState, Model, Command> {

    private final Table<String, Command, Double> qMatrix;
    private final List<? extends Command> possibleActions;

    private volatile @MonotonicNonNull Command lastAction;
    private volatile @MonotonicNonNull WorldState lastWorldState;

    @SuppressWarnings("initialization")
    public QLearningController() {
        super();
        this.qMatrix = HashBasedTable.create(getApproximateStateSpaceSize(), getPossibleActions().size()) ;
        this.possibleActions = new ArrayList<>((Collection<? extends Command>) getPossibleActions());
    }

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
        }

        this.lastWorldState = worldState;
        this.lastAction = bestAction.first;
    }

    protected synchronized Command chooseAction() {
        if (lastWorldState == null || Math.random() < getExploreFactor()) {
            return chooseRandomAction();
        }
        return getBestActionFor(qMatrix.row(encodeState(lastWorldState))).first;
    }

    protected Command tick(WorldState state) {
        applyWorldUpdate(state);
        return chooseAction();
    }

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

    protected @NonNull Command chooseRandomAction() {
        synchronized (this.possibleActions) {
            return this.possibleActions.get((int) (Math.random() * this.possibleActions.size()));
        }
    }

    private double getRewardOrDefault(@Nullable WorldState worldState, @Nullable Command action) {
        if (worldState == null || action == null) return getDefaultReward();
        synchronized (this.qMatrix) {
            Double val = qMatrix.get(encodeState(worldState), action);
            return val == null ? getDefaultReward() : val;
        }
    }

    @Pure
    protected abstract String encodeState(@NonNull WorldState state);

    @Pure
    @Constant
    protected abstract int getApproximateStateSpaceSize();

    @Pure
    protected abstract double getRewardFor(WorldState state, Command action);


    protected Scheduler createScheduler() {
        return Schedulers.newSingle("Controller");
    }

    @Pure
    protected @NonNull double getDefaultReward() {
        return 0;
    }

    @Pure
    protected double getSpeedFactor() {
        return 0.1;
    }

    @Pure
    protected double getDiscountFactor() {
        return 0.4;
    }

    @Pure
    protected double getExploreFactor() {
        return 0.2;
    }
}
