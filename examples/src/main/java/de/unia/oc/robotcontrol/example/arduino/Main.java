/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino;

import de.unia.oc.robotcontrol.device.Device;
import de.unia.oc.robotcontrol.device.I2CConnector;
import de.unia.oc.robotcontrol.example.arduino.data.ArduinoState;
import de.unia.oc.robotcontrol.example.arduino.data.RobotDrivingCommand;
import de.unia.oc.robotcontrol.example.arduino.device.DiscreteSimulatedRobot;
import de.unia.oc.robotcontrol.example.arduino.message.ArduinoMessageTypes;
import de.unia.oc.robotcontrol.example.arduino.message.SpeedCmdMessage;
import de.unia.oc.robotcontrol.example.arduino.message.UpdateRequestMessage;
import de.unia.oc.robotcontrol.example.arduino.oc.ArduinoController;
import de.unia.oc.robotcontrol.example.arduino.oc.ArduinoObserver;
import de.unia.oc.robotcontrol.example.arduino.oc.ManualController;
import de.unia.oc.robotcontrol.flow.strategy.IgnoringFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.LatestFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TypeFilterFlowStrategy;
import de.unia.oc.robotcontrol.message.*;
import de.unia.oc.robotcontrol.oc.Controller;
import de.unia.oc.robotcontrol.oc.ObservationModel;
import de.unia.oc.robotcontrol.oc.RobotControl;
import de.unia.oc.robotcontrol.util.Logger;
import de.unia.oc.robotcontrol.visualization.ObjectGrid;
import org.checkerframework.checker.nullness.qual.Nullable;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static final int DEFAULT_SPEED = 20;
    private static volatile @Nullable Message lastMessage = null;
    /**
     * @param args The program arguments. Can include the following words:
     *             - simulation | simulate: Don't try to connect to the arduino using I2C, but instead
     *             run a simulated version within a discrete grid.
     *
     * @throws IOException in case the program could not connect to the arduino using I2C (Bus 1, Device 4)
     */
    public static void main(String[] args) throws IOException {
        final Set<String> argSet = new HashSet<>(Arrays.asList(args));
        final boolean simulate = argSet.contains("simulate") || argSet.contains("simulation");
        final boolean manual = argSet.contains("manual");

        // startWithoutFacade(simulate, manual);
        startWithFacade(simulate, manual);
    }

    private static void startWithFacade(boolean simulate, boolean manual) throws IOException {
        // RobotControl<> control =
        final Controller<ArduinoState, ObservationModel<ArduinoState>, RobotDrivingCommand> controller = manual
                ? new ManualController()
                : new ArduinoController();
        final ArduinoObserver<ObservationModel<ArduinoState>> observer = new ArduinoObserver<>(controller.getObservationModel());

        RobotControl
                .build(observer, controller)
                .withActionInterpreter((c) -> new SpeedCmdMessage(c, 64))
                .withDevice(createDevice(simulate), ArduinoMessageTypes.SPEED_CMD)
                .registerObserverMessages(ArduinoMessageTypes.DISTANCE_DATA)
                .withMessageStrategy(ArduinoMessageTypes.DISTANCE_DATA, LatestFlowStrategy.create())
                .withMessageStrategy(ArduinoMessageTypes.SPEED_CMD, LatestFlowStrategy.create())
                .withMessageStrategy(ArduinoMessageTypes.UPDATE_REQUEST, IgnoringFlowStrategy.create())
                .create()
                .run();
    }

    private static Device<Message, Message> createDevice(boolean simulate) throws IOException {
        return simulate
                ?
                // define a simulated version of the arduino in a discrete grid environment
                new DiscreteSimulatedRobot(
                        ArduinoMessageTypes.ENCODING,
                        UpdateRequestMessage::new,
                        new ObjectGrid(20, 20)
                ) :

                // define the arduino which is connected using I2C
                new I2CConnector<>(
                        32,
                        1,
                        (byte) 4,
                        ArduinoMessageTypes.ENCODING,
                        ArduinoMessageTypes.ENCODING,
                        UpdateRequestMessage::new);
    }

}
