/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.unia.oc.robotcontrol.util.Tuple;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.signedness.qual.Constant;
import org.checkerframework.dataflow.qual.Pure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class QLearningController<I, M extends ObservationModel<I>, O> implements Controller<I, M, O> {

    private final Table<String, O, Double> qMatrix;
    private final List<@NonNull O> possibleActions;

    private volatile @MonotonicNonNull O lastAction;
    private volatile @MonotonicNonNull I lastWorldState;

    @SuppressWarnings("initialization")
    public QLearningController() {
        this.qMatrix = HashBasedTable.create(getApproximateStateSpaceSize(), getPossibleActions().size()) ;
        this.possibleActions = new ArrayList<>(getPossibleActions());
    }

    @RequiresNonNull("this.qMatrix")
    @EnsuresNonNull({"this.lastWorldState", "this.lastAction"})
    private synchronized void applyWorldUpdate(@NonNull I worldState) {
        String encoded = encodeState(worldState);
        Tuple<@NonNull O, @NonNull Double> bestAction = getBestActionFor(qMatrix.row(encoded));

        if (lastAction != null && lastWorldState != null) {
            String lastStateEncoded = encodeState(lastWorldState);

            double prevQ = getOrDefault(lastWorldState, lastAction);
            double reward = getRewardFor(worldState, bestAction.first);

            qMatrix.put(lastStateEncoded, lastAction,
                    prevQ + getSpeedFactor() * (reward + getDiscountFactor() * bestAction.second - prevQ));
        }

        this.lastWorldState = worldState;
        this.lastAction = bestAction.first;
    }

    private synchronized @NonNull O chooseAction() {
        if (lastWorldState == null || Math.random() < getExploreFactor()) {
            return chooseRandomAction();
        }
        return getBestActionFor(qMatrix.row(encodeState(lastWorldState))).first;
    }

    @SuppressWarnings("nullness")
    private Tuple<@NonNull O, @NonNull Double> getBestActionFor(Map<O, Double> actionMap) {
        if (actionMap.isEmpty()) return Tuple.create(chooseRandomAction(), getDefaultReward());
        double bestVal = - Double.MAX_VALUE;
        @MonotonicNonNull O bestAction = null;

        for (Map.Entry<O, Double> action : actionMap.entrySet()) {
            if (action.getValue() > bestVal) {
                bestVal = action.getValue();
                bestAction = action.getKey();
            }
        }

        return bestAction == null
                ? Tuple.create(chooseRandomAction(), getDefaultReward())
                : Tuple.create(bestAction, actionMap.get(bestAction));
    }

    private @NonNull O chooseRandomAction() {
        synchronized (this.possibleActions) {
            return this.possibleActions.get((int) (Math.random() * this.possibleActions.size()));
        }
    }

    private double getOrDefault(@Nullable I worldState, @Nullable O action) {
        if (worldState == null || action == null) return getDefaultReward();
        synchronized (this.qMatrix) {
            Double val = qMatrix.get(encodeState(worldState), action);
            return val == null ? getDefaultReward() : val;
        }
    }

    @Pure
    protected abstract String encodeState(@NonNull I state);

    @Pure
    @Constant
    protected abstract int getApproximateStateSpaceSize();

    @Pure
    protected abstract double getRewardFor(I state, O action);

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
