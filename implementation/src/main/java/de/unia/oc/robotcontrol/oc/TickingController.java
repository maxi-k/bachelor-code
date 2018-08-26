/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.BufferFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.LoggingFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.SchedulingFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TimedFlowStrategy;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

public abstract class TickingController<WorldState extends Object,
        Model extends ObservationModel<WorldState>,
        Command extends Object>
        implements Controller<WorldState, Model, Command> {

    protected final Scheduler scheduler;
    protected final Model observationModel;

    private @MonotonicNonNull Observer<?, ? extends WorldState, ? super Model> observer;

    public TickingController(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.observationModel = createObservationModel();
    }

    public TickingController(Executor executor) {
         this(Schedulers.fromExecutor(executor));
    }

    public TickingController() {
        this(Schedulers.newSingle("TickingController"));
    }

    protected abstract Command tick(WorldState worldState);

    protected abstract int getCommandBufferSize();

    protected abstract int getTargetTickFreqMs(@UnderInitialization TickingController<WorldState, Model, Command> this);

    protected BufferOverflowStrategy getBufferOverflowStrategy() {
        return BufferOverflowStrategy.DROP_LATEST;
    }

    @Override
    public FlowStrategy<WorldState, Command> getFlowStrategy() {
        return BufferFlowStrategy
                .<WorldState>create(getCommandBufferSize(), BufferOverflowStrategy.DROP_OLDEST)
                .with(SchedulingFlowStrategy.create(scheduler))
                .with(TimedFlowStrategy.createDurational(getObservationModel().getTargetUpdateTime(),
                        this::getInitialState,
                        (l, s) -> tick(s))
                );
    }

    protected abstract Model createObservationModel(@UnderInitialization TickingController<WorldState, Model, Command> this);

    protected abstract WorldState getInitialState();

    @Override
    public Model getObservationModel() {
        return this.observationModel;
    }

    @Override
    public void setObserver(Observer<?, ? extends WorldState, ? super Model> observer) {
        this.observer = observer;
        this.observer.setObservationModel(this.observationModel);
    }
}
