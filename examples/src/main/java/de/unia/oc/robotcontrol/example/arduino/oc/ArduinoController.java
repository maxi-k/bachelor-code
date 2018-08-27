/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.oc;

import de.unia.oc.robotcontrol.example.arduino.data.ArduinoState;
import de.unia.oc.robotcontrol.example.arduino.data.RobotDrivingCommand;
import de.unia.oc.robotcontrol.oc.ObservationModel;
import de.unia.oc.robotcontrol.oc.QLearningController;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Constant;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArduinoController extends QLearningController<ArduinoState, ObservationModel<ArduinoState>, RobotDrivingCommand> {

    private final static int DEFAULT_SPEED = 20;

    @Override
    protected int getCommandBufferSize() {
        return 32;
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
        int minAcceptableDist = 1; //mm
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
    protected int getTargetTickFreqMs(@UnderInitialization ArduinoController this) {
        return 40;
    }

    @Override
    protected ObservationModel<ArduinoState> createObservationModel(@UnderInitialization ArduinoController this) {
        return new ObservationModel<ArduinoState>() {
            @Override
            public Duration getTargetUpdateTime() {
                return Duration.ofMillis(getTargetTickFreqMs());
            }
        };
    }

    @Override
    protected ArduinoState getInitialState() {
        return ArduinoState.createEmpty();
    }

    @Override
    public @Constant Set<RobotDrivingCommand> getPossibleActions() {
        Set<RobotDrivingCommand> result = new HashSet<>(Arrays.asList(RobotDrivingCommand.values()));
        return Collections.unmodifiableSet(result);
    }

}
