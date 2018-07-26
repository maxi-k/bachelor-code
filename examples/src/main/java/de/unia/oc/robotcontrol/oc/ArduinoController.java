/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.concurrent.Timespan;
import de.unia.oc.robotcontrol.data.ArduinoState;
import de.unia.oc.robotcontrol.data.RobotDrivingCommand;
import de.unia.oc.robotcontrol.flow.InFlows;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.message.SpeedCmdMessage;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.checkerframework.checker.signedness.qual.Constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ArduinoController extends QLearningController<ArduinoState, ObservationModel<ArduinoState>, RobotDrivingCommand> {

    private ObservationModel<ArduinoState> model;
    private @MonotonicNonNull Observer<ArduinoState, ObservationModel<ArduinoState>> observer;

    private final static int DEFAULT_SPEED = 20;
    private final PassiveInFlow<RobotDrivingCommand> actuator;

    @EnsuresNonNull({"this.model", "this.actuator"})
    public ArduinoController(PassiveInFlow<? super SpeedCmdMessage> next) {
        super();
        this.actuator = InFlows.createUnbuffered((RobotDrivingCommand msg) -> {
                    // System.out.println("Controller sent out driving command: " + msg);
                    next.accept(new SpeedCmdMessage(msg, DEFAULT_SPEED));
                }
        );
        this.model = createObservationModel();
    }

    @Override
    protected String encodeState(@NonNull ArduinoState state) {
        return state.encode();
    }

    @Override
    protected @Constant int getApproximateStateSpaceSize() {
        return (int) Math.pow(10, 6);
    }

    @Override
    protected double getRewardFor(ArduinoState state, RobotDrivingCommand action) {
        int minAcceptableDist = 5; //mm
        switch(action) {
            case FRONT:
                if (state.getFrontDist() <= minAcceptableDist) return -1;
                return 0;
            case LEFT:
                if (state.getLeftDist() <= minAcceptableDist) return -1;
                return 0;
            case RIGHT:
                if (state.getRightDist() <= minAcceptableDist) return -1;
                return 0;
            case STOP:
                if (state.getFrontDist() <= minAcceptableDist ||
                        state.getRightDist() <= minAcceptableDist ||
                        state.getLeftDist() <= minAcceptableDist) {
                    return 1;
                }
                return -5;
            case ROTATE:
                if (state.getFrontDist() <= minAcceptableDist) return 2;
                return -5;
            default:
                return 0;
        }
    }

    @Override
    public ObservationModel<ArduinoState> getObservationModel() {
        return this.model;
    }

    @Override
    public void setObserver(Observer<ArduinoState, ObservationModel<ArduinoState>> observer) {
        this.observer = observer;
        this.observer.setObservationModel(model);
    }

    @Override
    public @Constant Set<RobotDrivingCommand> getPossibleActions() {
        Set<RobotDrivingCommand> result = new HashSet<>(Arrays.asList(RobotDrivingCommand.values()));
        return Collections.unmodifiableSet(result);
    }

    @RequiresNonNull({"this.actuator"})
    private ObservationModel<ArduinoState> createObservationModel(@UnderInitialization(QLearningController.class) ArduinoController this) {
        ArduinoController self = this;
        return new ObservationModel<ArduinoState>() {

            @Override
            public Timespan getTargetUpdateTime() {
                return Timespan.create(40, TimeUnit.MILLISECONDS);
            }

            @Override
            @SuppressWarnings("initialization")
            public void accept(ArduinoState arduinoState) {
                self.inFlow().accept(arduinoState);
                self.outFlow().get().accept(actuator);
            }
        };
    }
}
