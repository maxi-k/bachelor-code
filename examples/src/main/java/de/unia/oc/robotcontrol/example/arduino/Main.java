/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino;

import com.pi4j.util.Console;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.concurrent.Scheduling;
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
import de.unia.oc.robotcontrol.flow.strategy.CastMapFlowStrategy;
import de.unia.oc.robotcontrol.message.*;
import de.unia.oc.robotcontrol.oc.ObservationModel;
import de.unia.oc.robotcontrol.util.Logger;
import de.unia.oc.robotcontrol.visualization.ObjectGrid;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int DEFAULT_SPEED = 20;
    private static volatile @Nullable Message lastMessage = null;
    /**
     * Control the arduino using
     * - w (forward)
     * - a (left)
     * - s (stop)
     * - d (right)
     * - r (rotate)
     * -- p to print the last received arduino message
     * @param args The program arguments. Can include the following words:
     *             - simulation | simulate: Don't try to connect to the arduino using I2C, but instead
     *             run a simulated version within a discrete grid.
     *
     * @throws IOException in case the program could not connect to the arduino using I2C (Bus 1, Device 4)
     */
    public static void main(String[] args) throws IOException {

        final Set<String> argSet = new HashSet<>(Arrays.asList(args));

        // start Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
        final Console console = new Console();

        // print program title/header
        console.title("<-- Observer/Controller Robot-Control -->", "I2C Example");

        // allow for user to exit program using CTRL-C
        console.promptForExit();


        // Define a recipient for the arduino messages
        // which executes the given callback
        final CallbackMessageRecipient<Message> printer = new CallbackMessageRecipient<>((msg) -> {
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

        MessageMulticast<Message> dispatcher = new EmittingMessageMulticast<>();

        // dispatcher.register(ErrorMessage.errorMessageType, printer);

        // define a schedule for how often the raspberry pi should
        // ask for updates on the arduino
        final ScheduleProvider schedule = Scheduling.interval(
                Executors.newScheduledThreadPool(1),
                40,
                TimeUnit.MILLISECONDS
        );

        final LockingDeviceConnector<Message, Message> arduino = (argSet.contains("simulate") || argSet.contains("simulation"))
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

        arduino.asPublisher().subscribe(dispatcher.asSubscriber());
        dispatcher.subscribe(ArduinoMessageTypes.SPEED_CMD, arduino.asSubscriber());

        if (argSet.contains("manual")) {
            setupManual(console, dispatcher, printer);
        } else {
            setupControlled(dispatcher);
        }

    }

    private static void setupControlled(MessageMulticast<Message> dispatcher) {
        final ArduinoController controller = new ArduinoController();
        final ArduinoObserver<ObservationModel<ArduinoState>> observer = new ArduinoObserver<>(controller.getObservationModel());
        controller.setObserver(observer);
        Flux.from(dispatcher
                .subscribeTo(ArduinoMessageTypes.DISTANCE_DATA))
                .transform(CastMapFlowStrategy.create(SensorMessage.class))
                .<ArduinoState>transform(observer::apply)
                .<RobotDrivingCommand>transform(controller::apply)
                .map((c) -> new SpeedCmdMessage(c, DEFAULT_SPEED))
                .subscribe(new Subscriber<SpeedCmdMessage>() {
                    private @MonotonicNonNull Subscription s;
                    @Override
                    public void onSubscribe(Subscription s) {
                        this.s = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(SpeedCmdMessage speedCmdMessage) {
                        Logger.instance().debug("SpeedCmdMessage: " + speedCmdMessage);
                        if (s != null) s.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Logger.instance().logException(t);
                    }

                    @Override
                    public void onComplete() {
                        s = null;
                    }
                });
    }

    private static void setupManual(Console console, MessageMulticast<Message> dispatcher,
                                                   MessageRecipient<Message> printer) {
        // read user commands and send them to the arduino constantly
        dispatcher.subscribe(ArduinoMessageTypes.DISTANCE_DATA, printer.asSubscriber());
        console.println("Press 'q' to stop, p to print the last received message");
        try (Scanner reader = new Scanner(System.in)) {
            while (true) {
                try {
                    console.println("Enter a message: ");
                    String read = reader.next();
                    char first = read.charAt(0);
                    if (first == 'q') break;
                    if (first == 'p') {
                        console.println(lastMessage != null ? lastMessage.toString() : "No last Message");
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
