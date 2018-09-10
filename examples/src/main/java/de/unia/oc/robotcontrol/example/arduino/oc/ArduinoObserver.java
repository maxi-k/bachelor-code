/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.oc;

import de.unia.oc.robotcontrol.concurrent.Clock;
import de.unia.oc.robotcontrol.concurrent.EmittingClock;
import de.unia.oc.robotcontrol.concurrent.TimeProvider;
import de.unia.oc.robotcontrol.example.arduino.data.ArduinoState;
import de.unia.oc.robotcontrol.example.arduino.message.DistanceDataMessage;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.function.PublisherTransformation;
import de.unia.oc.robotcontrol.flow.strategy.LatestFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.LoggingFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.SchedulingFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TimedFlowStrategy;
import de.unia.oc.robotcontrol.message.SensorMessage;
import de.unia.oc.robotcontrol.oc.ObservationModel;
import de.unia.oc.robotcontrol.oc.Observer;
import org.checkerframework.checker.nullness.qual.NonNull;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class ArduinoObserver<T extends ObservationModel<ArduinoState>>
        implements Observer<SensorMessage, ArduinoState, T> {

    private final Scheduler executor;

    private @NonNull T observationModel;
    private long lastUpdatedTime;

    private final FlowStrategy<SensorMessage, ArduinoState> flowStrategy;

    private ArduinoState lastComputedState;
    private final Clock timeSupplier;

    @SuppressWarnings("initialization")
    public ArduinoObserver(@NonNull T observationModel) {
        this.observationModel = observationModel;
        this.lastComputedState = ArduinoState.createEmpty();
        this.lastUpdatedTime = System.currentTimeMillis();

        this.timeSupplier = EmittingClock.create(observationModel.getTargetUpdateTime());
        this.executor = Schedulers.newSingle("Observer Executor");

        timeSupplier.setInterval(observationModel.getTargetUpdateTime());

        this.flowStrategy = LatestFlowStrategy
                .<SensorMessage>create()
                .with(SchedulingFlowStrategy.create(executor))
                .with(PublisherTransformation.liftPublisher(this::acceptData))
                .with(TimedFlowStrategy.createTimed(timeSupplier.getTicks(), () -> lastComputedState));
    }

    private ArduinoState acceptData(SensorMessage data) {
        System.out.println("observer received data: " + data);
        this.lastComputedState.updateWith(data);
        this.lastUpdatedTime = System.currentTimeMillis();
        return lastComputedState;
    }

    @Override
    public synchronized T getObservationModel() {
        return this.observationModel;
    }

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
    public Class<SensorMessage> getAcceptedClass() {
        return SensorMessage.class;
    }
}
