/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol;

import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;
import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.device.I2CConnector;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.message.SpeedCmdMessage;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Scanner;

public class Main {

    public static int ARDUINO_INT_SIZE = 2;
    public static ByteOrder ARDUINO_BYTE_ORDER = ByteOrder.BIG_ENDIAN;

    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException {

        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
        final Console console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "I2C Example");

        // allow for user to exit program using CTRL-C
        console.promptForExit();
        try (Scanner reader = new Scanner(System.in)) {
            while (true) {
                try {
                    console.println("Enter a message: ");
                    String read = reader.next();
                    console.println("READ: " + read);
                    char first = read.charAt(0);
                    long now = System.currentTimeMillis();

                    Encoding<Message> encoder = ArduinoMessageType.SpeedCommand.instance().asEncoding();

                    I2CConnector arduino = new I2CConnector(32, 1, (byte) 4, encoder);
                    arduino.sendMessage(new SpeedCmdMessage(first));

                    SpeedCmdMessage result = (SpeedCmdMessage) arduino.getMessage();

                    // ∂t ist konsistent zwischen 6 und 7 millisekunden
                    // außer bei der ersten kommunikation (~400ms).
                    // vermutung: i2c protokoll *oder* jvm optimierung der
                    // neu allokierten objekte im while loop
                    console.println("∂t:" + (System.currentTimeMillis() - now));
                    console.print("Arduino: ");
                    console.print(result.toString());
                    console.emptyLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
