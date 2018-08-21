/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino;

import com.pi4j.util.Console;
import de.unia.oc.robotcontrol.device.Device;
import de.unia.oc.robotcontrol.device.I2CConnector;
import de.unia.oc.robotcontrol.device.LockingDeviceConnector;
import de.unia.oc.robotcontrol.example.arduino.data.ArduinoState;
import de.unia.oc.robotcontrol.example.arduino.data.RobotDrivingCommand;
import de.unia.oc.robotcontrol.example.arduino.device.DiscreteSimulatedRobot;
import de.unia.oc.robotcontrol.example.arduino.message.ArduinoMessageTypes;
import de.unia.oc.robotcontrol.example.arduino.message.SpeedCmdMessage;
import de.unia.oc.robotcontrol.example.arduino.message.UpdateRequestMessage;
import de.unia.oc.robotcontrol.example.arduino.oc.ArduinoController;
import de.unia.oc.robotcontrol.example.arduino.oc.ArduinoObserver;
import de.unia.oc.robotcontrol.flow.strategy.IgnoringFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.LatestFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TypeFilterFlowStrategy;
import de.unia.oc.robotcontrol.message.*;
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
        final ArduinoController controller = new ArduinoController();
        final ArduinoObserver<ObservationModel<ArduinoState>> observer = new ArduinoObserver<>(controller.getObservationModel());

        RobotControl
                .build(observer, controller)
                .withActionInterpreter((c) -> new SpeedCmdMessage(c, 20))
                .withDevice(createDevice(simulate), ArduinoMessageTypes.SPEED_CMD)
                .registerObserverMessages(ArduinoMessageTypes.DISTANCE_DATA)
                .withMessageStrategy(ArduinoMessageTypes.DISTANCE_DATA, LatestFlowStrategy.create())
                .withMessageStrategy(ArduinoMessageTypes.SPEED_CMD, LatestFlowStrategy.create())
                .withMessageStrategy(ArduinoMessageTypes.UPDATE_REQUEST, IgnoringFlowStrategy.create())
                .create()
                .run();
    }

    private static void startWithoutFacade(boolean simulate, boolean manual) throws IOException {

        // Define a recipient for the arduino messages
        // which executes the given callback
        final MessageRecipient<Message> printer = createPrinter();

        MessageMulticast<Message> dispatcher = new EmittingMessageMulticast<>();

        // dispatcher.register(ErrorMessage.errorMessageType, printer);

        final Device<Message, Message> arduino = createDevice(simulate);
        arduino.asPublisher().subscribe(dispatcher.asSubscriber());
        dispatcher.subscribe(ArduinoMessageTypes.SPEED_CMD, arduino.asSubscriber());

        if (manual) {
            setupManual(dispatcher, printer);
        } else {
            setupControlled(dispatcher);
        }

    }

    private static MessageRecipient<Message> createPrinter() {
        return new CallbackMessageRecipient<>((msg) -> {
            // ∂t ist konsistent zwischen 6 und 7 millisekunden
            // außer bei der ersten kommunikation (~400ms).
            // vermutung: i2c protokoll *oder* jvm optimierung der
            // neu allokierten objekte im while loop
            // console.println("∂t:" + (System.currentTimeMillis() - now));
            // console.print("Arduino: ");
            // console.print(msg.toString());
            // console.emptyLine();
            System.out.println(msg);
            lastMessage = msg;
        });
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

    private static void setupControlled(MessageMulticast<Message> dispatcher) {
        final ArduinoController controller = new ArduinoController();
        final ArduinoObserver<ObservationModel<ArduinoState>> observer = new ArduinoObserver<>(controller.getObservationModel());
        controller.setObserver(observer);
        Flux.from(dispatcher.subscribeToAll())
                .transform(TypeFilterFlowStrategy.create(SensorMessage.class))
                .<ArduinoState>transform(observer::apply)
                .<RobotDrivingCommand>transform(controller::apply)
                .map((c) -> new SpeedCmdMessage(c, DEFAULT_SPEED))
                .subscribe(dispatcher.asSubscriber());
    }

    /**
     * Control the arduino using
     * - w (forward)
     * - a (left)
     * - s (stop)
     * - d (right)
     * - r (rotate)
     * -- p to print the last received arduino message
     **/
    private static void setupManual(MessageMulticast<Message> dispatcher,
                                    MessageRecipient<Message> printer) {
        // read user commands and send them to the arduino constantly
        dispatcher.subscribe(ArduinoMessageTypes.DISTANCE_DATA, printer.asSubscriber());
        Logger.instance().println("Press 'q' to stop, p to print the last received message");
        try (Scanner reader = new Scanner(System.in)) {
            while (true) {
                try {
                    Logger.instance().println("Enter a message: ");
                    String read = reader.next();
                    char first = read.charAt(0);
                    if (first == 'q') break;
                    if (first == 'p') {
                        Logger.instance().println(lastMessage != null ? lastMessage.toString() : "No last Message");
                        continue;
                    }
                    // send the read command as a driving command to the arduino,
                    // with the driving direction specified by the read character
                    // with a fixed speed of 20 mmps
                    dispatcher.multicast(new SpeedCmdMessage(first, DEFAULT_SPEED));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Stopping...");
    }
}
