/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.oc;

import de.unia.oc.robotcontrol.concurrent.ClockType;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.concurrent.Scheduling;
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
import reactor.core.publisher.UnicastProcessor;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ArduinoObserver<T extends ObservationModel<ArduinoState>> implements Observer<SensorMessage, ArduinoState, T> {

    private final ScheduleProvider schedule;

    private @NonNull T observationModel;
    private long lastUpdatedTime;

    private final FlowStrategy<SensorMessage, ArduinoState> flowStrategy;

    private ArduinoState lastComputedState;
    private final UnicastProcessor<Duration> timeSupplier;

    @SuppressWarnings("initialization")
    public ArduinoObserver(@NonNull T observationModel) {
        this.observationModel = observationModel;
        this.lastComputedState = ArduinoState.createEmpty();
        this.lastUpdatedTime = System.currentTimeMillis();

        this.timeSupplier = UnicastProcessor.create();
        this.schedule = Scheduling.interval(
                Executors.newScheduledThreadPool(1),
                observationModel.getTargetUpdateTime().toMillis(),
                TimeUnit.MILLISECONDS
        );

        timeSupplier.onNext(observationModel.getTargetUpdateTime());

        this.flowStrategy = LatestFlowStrategy
                .<SensorMessage>create()
                .with(SchedulingFlowStrategy.create(schedule.getExecutor()))
                .with(PublisherTransformation.liftPublisher(this::acceptData))
                .with(TimedFlowStrategy.createDurational(timeSupplier));
    }

    private ArduinoState acceptData(SensorMessage data) {
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
        timeSupplier.onNext(model.getTargetUpdateTime());
    }

    @Override
    public synchronized ArduinoState getModelState() {
        return lastComputedState;
    }

    public ScheduleProvider getSchedule() {
        return schedule;
    }

    @Override
    public FlowStrategy<SensorMessage, ArduinoState> getFlowStrategy() {
        return flowStrategy;
    }

    @Override
    public ClockType getClockType() {
        return ClockType.INTERNAL;
    }
}
