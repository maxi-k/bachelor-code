/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.concurrent.Clockable;
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

/**
 * A Controller implementation for tasks which can be expressed as a tick-function,
 * that is, a function which transforms the {@link WorldState} received from the
 * {@link Observer} into a {@link Command} by using a simple function ({@link #tick(Object)}).
 *
 * This implementation is clocked, so it tries to update in a fixed interval, as set
 * by {@link #getTargetTickFreqMs()}.
 *
 * @param <WorldState> the type of the report received from the observer
 * @param <Model> the type of the observation model used
 * @param <Command> the type of the command emitted by this controller
 */
public abstract class TickingController<WorldState extends Object,
        Model extends ObservationModel<WorldState>,
        Command extends Object>
        implements Controller<WorldState, Model, Command>, Clockable {

    /**
     * The execution context used by this controller
     */
    protected final Scheduler scheduler;
    /**
     * The current observation model instance
     */
    protected final Model observationModel;

    /**
     * The observer that was set that provides {@link WorldState} values
     * to this controller.
     */
    private @MonotonicNonNull Observer<?, ? extends WorldState, ? super Model> observer;

    /**
     * Create a new instance of {@link TickingController} which runs
     * on the given {@link Scheduler}
     * @param scheduler the scheduler to run this on
     */
    public TickingController(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.observationModel = createObservationModel();
    }

    /**
     * Create a new instance of {@link TickingController} which runs
     * on the given {@link Executor}, which is wrapped
     * in a {@link Scheduler}
     * @param executor the executor to run this on
     */
    public TickingController(Executor executor) {
         this(Schedulers.fromExecutor(executor));
    }

    /**
     * Creates a new instance of {@link TickingController} which
     * runs on its own thread.
     */
    public TickingController() {
        this(Schedulers.newSingle("TickingController"));
    }

    /**
     * The tick function which transforms a {@link WorldState} received from
     * the observer into a {@link Command}.
     * @param worldState the state to update this with
     * @return the command to publish
     */
    protected abstract Command tick(WorldState worldState);

    /**
     * @return the default buffer size of the buffer of
     * incoming messages is full
     */
    protected abstract int getCommandBufferSize();

    /**
     * @return the target frequency of reports to be received by the observer
     * in milliseconds.
     */
    protected abstract int getTargetTickFreqMs(@UnderInitialization TickingController<WorldState, Model, Command> this);

    /**
     * @return the strategy to apply when the message buffer
     * is full. The default ist {@link BufferOverflowStrategy#DROP_OLDEST}
     */
    protected BufferOverflowStrategy getBufferOverflowStrategy() {
        return BufferOverflowStrategy.DROP_OLDEST;
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

    /**
     * Create the observation model when this is initialized.
     * @return a new instance of {@link Model}
     */
    protected abstract Model createObservationModel(@UnderInitialization TickingController<WorldState, Model, Command> this);

    /**
     * Create a initial state. This is required because the controller
     * might 'tick' on the target frequency before a value was received
     * from the observer.
     * @return a new 'default' instance of {@link WorldState}
     */
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

    @Override
    public ClockType getClockType() {
        return ClockType.INTERNAL;
    }
}
