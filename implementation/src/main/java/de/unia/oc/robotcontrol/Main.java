/* 2016 */
package de.unia.oc.robotcontrol;

import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;
import de.unia.oc.robotcontrol.coding.CharEncoding;
import de.unia.oc.robotcontrol.coding.IntegerEncoding;
import de.unia.oc.robotcontrol.coding.ListEncoding;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;
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

                    Communicator.instance().sendMessage(first, new CharEncoding()::encode);
                    ListEncoding<Integer> encoding = new ListEncoding<>(new IntegerEncoding(), 3);
                    List<Integer> result = Communicator.instance().receiveMessage(encoding::decode);

                    // ∂t ist konsistent zwischen 6 und 7 millisekunden
                    // außer bei der ersten kommunikation (~400ms).
                    // vermutung: i2c protokoll *oder* jvm optimierung der
                    // neu allokierten objekte im while loop
                    console.println("∂t:" + (System.currentTimeMillis() - now));
                    console.print("Arduino: ");
                    for (int item : result) {
                        console.print(item + " ");
                    }
                    console.emptyLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
