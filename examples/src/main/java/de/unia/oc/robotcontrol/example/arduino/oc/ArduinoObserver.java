/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.oc;

import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.concurrent.Scheduling;
import de.unia.oc.robotcontrol.example.arduino.data.ArduinoState;
import de.unia.oc.robotcontrol.flow.old.ActiveOutFlow;
import de.unia.oc.robotcontrol.flow.old.InFlows;
import de.unia.oc.robotcontrol.flow.old.OutFlows;
import de.unia.oc.robotcontrol.flow.old.PassiveInFlow;
import de.unia.oc.robotcontrol.message.SensorMessage;
import de.unia.oc.robotcontrol.oc.ObservationModel;
import de.unia.oc.robotcontrol.oc.Observer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.Executors;

public class ArduinoObserver<T extends ObservationModel<ArduinoState>> implements Observer<ArduinoState, T> {


    private final ScheduleProvider schedule;
    private final ActiveOutFlow<ArduinoState> outFlow;
    private final PassiveInFlow<SensorMessage> inFlow;

    private @NonNull T observationModel;
    private long lastUpdatedTime;
    private ArduinoState lastComputedState;

    @SuppressWarnings("initialization")
    public ArduinoObserver(@NonNull T observationModel) {
        this.observationModel = observationModel;
        this.lastComputedState = ArduinoState.createEmpty();
        this.lastUpdatedTime = System.currentTimeMillis();

        this.schedule = Scheduling.interval(
                Executors.newScheduledThreadPool(1),
                this.observationModel.getTargetUpdateTime().getTime(),
                this.observationModel.getTargetUpdateTime().getUnit()
        );

        this.outFlow = OutFlows.createScheduled(schedule, this::getModelState, InFlows.createUnbuffered(observationModel));
        this.inFlow = InFlows.createUnbuffered(this::acceptData);
    }

    private void acceptData(SensorMessage data) {
        this.lastComputedState.updateWith(data);
        this.lastUpdatedTime = System.currentTimeMillis();
    }

    @Override
    public synchronized T getObservationModel() {
        return this.observationModel;
    }

    @Override
    public synchronized void setObservationModel(T model) {
        this.observationModel = model;
    }

    @Override
    public synchronized ArduinoState getModelState() {
        return lastComputedState;
    }

    @Override
    public PassiveInFlow<SensorMessage> inFlow() {
        return this.inFlow;
    }

    @Override
    public ActiveOutFlow<ArduinoState> outFlow() {
        return this.outFlow;
    }

    public ScheduleProvider getSchedule() {
        return schedule;
    }
}
