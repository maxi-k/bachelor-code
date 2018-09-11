/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.oc;

import de.unia.oc.robotcontrol.concurrent.Clock;
import de.unia.oc.robotcontrol.concurrent.EmittingClock;
import de.unia.oc.robotcontrol.concurrent.TimeProvider;
import de.unia.oc.robotcontrol.example.arduino.data.ArduinoState;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.function.PublisherTransformation;
import de.unia.oc.robotcontrol.flow.strategy.LatestFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.SchedulingFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TimedFlowStrategy;
import de.unia.oc.robotcontrol.message.SensorMessage;
import de.unia.oc.robotcontrol.oc.ObservationModel;
import de.unia.oc.robotcontrol.oc.Observer;
import org.checkerframework.checker.nullness.qual.NonNull;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import static de.unia.oc.robotcontrol.concurrent.Concurrent.ConcurrencyType.INTERNAL;

/**
 * The Observer for the Arduino example.
 * Compiles the received messages into an {@link ArduinoState}
 *
 * @param <T> The type of observation model to use
 */
public class ArduinoObserver<T extends ObservationModel<ArduinoState>>
        implements Observer<SensorMessage, ArduinoState, T> {

    /**
     * The scheduler to run this on
     */
    private final Scheduler executor;

    /**
     * The current observation model
     */
    private @NonNull T observationModel;

    /**
     * The timestamp at which this was last updated.
     * Not currently used.
     */
    private long lastUpdatedTime;

    /**
     * The {@link FlowStrategy} to use and expose by {@link #getFlowStrategy()}
     */
    private final FlowStrategy<SensorMessage, ArduinoState> flowStrategy;

    /**
     * The last state that was computed
     */
    private ArduinoState lastComputedState;

    /**
     * The clock returned by {@link #getTimeProvider()}
     */
    private final Clock timeSupplier;

    @SuppressWarnings("initialization")
    public ArduinoObserver(@NonNull T observationModel) {
        this.observationModel = observationModel;
        this.lastComputedState = ArduinoState.createEmpty();
        this.lastUpdatedTime = System.currentTimeMillis();

        // create a new clock to be used as time supplier
        this.timeSupplier = EmittingClock.create(observationModel.getTargetUpdateTime());
        this.executor = Schedulers.newSingle("Observer Executor");

        timeSupplier.setInterval(observationModel.getTargetUpdateTime());

        // the flow strategy should...
        this.flowStrategy = LatestFlowStrategy
                .<SensorMessage>create()
                // be executed on the observers own executor
                .with(SchedulingFlowStrategy.create(executor))
                // transform received data using the acceptData function
                .with(PublisherTransformation.liftPublisher(this::acceptData))
                // be clocked by the time supplier as set by the controller,
                // and return the last computed state
                .with(TimedFlowStrategy.createTimed(timeSupplier.getTicks(), () -> lastComputedState));
    }

    /**
     * Accept the given SensorData message, and update
     * the state reported to the controller with it.
     * @param data the received message, used for updating
     * @return the updated arduino state
     */
    private ArduinoState acceptData(SensorMessage data) {
        this.lastComputedState.updateWith(data);
        this.lastUpdatedTime = System.currentTimeMillis();
        return lastComputedState;
    }

    @Override
    public synchronized T getObservationModel() {
        return this.observationModel;
    }

    /**
     * Set the current observation model.
     * Als update the internal state, in particular {@link #timeSupplier}
     * to reflect the possibly change target update time.
     * @param model
     */
    @Override
    public synchronized void setObservationModel(T model) {
        this.observationModel = model;
        timeSupplier.setInterval(model.getTargetUpdateTime());
    }

    @Override
    public synchronized ArduinoState getModelState() {
        return lastComputedState;
    }

    @Override
    public TimeProvider getTimeProvider() {
        return this.timeSupplier;
    }

    @Override
    public FlowStrategy<SensorMessage, ArduinoState> getFlowStrategy() {
        return flowStrategy;
    }

    @Override
    public ConcurrencyType getConcurrencyType() {
        return INTERNAL;
    }

    @Override
    public Class<SensorMessage> getAcceptedClass() {
        return SensorMessage.class;
    }
}
