/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol;

import com.pi4j.util.Console;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.concurrent.Scheduling;
import de.unia.oc.robotcontrol.device.DiscreteSimulatedRobot;
import de.unia.oc.robotcontrol.message.*;
import de.unia.oc.robotcontrol.visualization.ObjectGrid;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static volatile Message lastMessage = null;
    /**
     * Control the arduino using
     * - w (forward)
     * - a (left)
     * - s (stop)
     * - d (right)
     * - r (rotate)
     * -- p to print the last received arduino message
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        // start Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
        final Console console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "I2C Example");

        // allow for user to exit program using CTRL-C
        console.promptForExit();


        // Define a recipient for the arduino messages
        // which executes the given callback
        final CallbackMessageRecipient printer = new CallbackMessageRecipient((msg) -> {
            // ∂t ist konsistent zwischen 6 und 7 millisekunden
            // außer bei der ersten kommunikation (~400ms).
            // vermutung: i2c protokoll *oder* jvm optimierung der
            // neu allokierten objekte im while loop
            // console.println("∂t:" + (System.currentTimeMillis() - now));
            // console.print("Arduino: ");
            // console.print(msg.toString());
            // console.emptyLine();
            lastMessage = msg;
        });

        // define a schedule for how often the raspberry pi should
        // ask for updates on the arduino
        final ScheduleProvider schedule = Scheduling.interval(
                Executors.newScheduledThreadPool(1),
                40,
                TimeUnit.MILLISECONDS
        );


        ObjectGrid grid = new ObjectGrid(20, 20);

        // define the arduino which is connected using I2C
        /*
        final I2CConnector arduino = new I2CConnector(
                32,
                1,
                (byte) 4,
                ArduinoMessageTypes.ENCODING,
                schedule,
                printer.inFlow(),
                UpdateRequestMessage::new);
                */
        final DiscreteSimulatedRobot arduino = new DiscreteSimulatedRobot(
                ArduinoMessageTypes.ENCODING,
                schedule,
                printer.inFlow(),
                UpdateRequestMessage::new,
                grid
        );

        // read user commands and send them to the arduino constantly
        console.println("Press 'q' to stop, p to print the last received message");
        try (Scanner reader = new Scanner(System.in)) {
            while (true) {
                try {
                    console.println("Enter a message: ");
                    String read = reader.next();
                    char first = read.charAt(0);
                    if (first == 'q') break;
                    if (first == 'p') {
                        console.println(lastMessage.toString());
                        continue;
                    }
                    // send the read command as a message to the arduino
                    // with a fixed speed of 20
                    arduino.inFlow().accept(new SpeedCmdMessage(first, 20));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Stopping...");
        try {
            schedule.terminate(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            schedule.terminate();
        }
    }
}
