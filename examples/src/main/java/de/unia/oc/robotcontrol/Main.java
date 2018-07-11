/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol;

import com.pi4j.util.Console;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.concurrent.Scheduling;
import de.unia.oc.robotcontrol.device.I2CConnector;
import de.unia.oc.robotcontrol.message.ArduinoMessageTypes;
import de.unia.oc.robotcontrol.message.CallbackMessageRecipient;
import de.unia.oc.robotcontrol.message.SpeedCmdMessage;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    /**
     * Control the arduino using
     * - w (forward)
     * - a (left)
     * - s (stop)
     * - d (right)
     * - r (rotate)
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

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
            console.print("Arduino: ");
            console.print(msg.toString());
            console.emptyLine();
        });

        // define a schedule for how often the raspberry pi should
        // ask for updates on the arduino
        final ScheduleProvider schedule = Scheduling.interval(
                Executors.newScheduledThreadPool(2),
                20,
                TimeUnit.MILLISECONDS
        );

        // define the arduino which is connected using I2C
        final I2CConnector arduino = new I2CConnector(
                32,
                1,
                (byte) 4,
                ArduinoMessageTypes.ENCODING,
                schedule,
                printer.inFlow());

        // read user commands and send them to the arduino constantly
        console.println("Press 'q' to stop.");
        try (Scanner reader = new Scanner(System.in)) {
            while (true) {
                try {
                    console.println("Enter a message: ");
                    String read = reader.next();
                    char first = read.charAt(0);
                    if (first == 'q') break;
                    // send the read command as a message to the arduino
                    // with a fixed speed of 20
                    arduino.inFlow().accept(new SpeedCmdMessage(first, (byte) 20));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
