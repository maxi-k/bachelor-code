/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.*;
import de.unia.oc.robotcontrol.util.Bijection;
import de.unia.oc.robotcontrol.util.Tuple;

import java.util.Arrays;
import java.util.List;

public final class ArduinoMessageTypes {

    private ArduinoMessageTypes() {}

    /**
     * Holds the coding-context in which the arduino is run
     */
    public static final CodingContext CONTEXT = CodingContext.ARDUINO;

    /**
     * MessageType for {@link DistanceDataMessage}. [int, int, int] -> [x, y, z]
     */
    public static final MessageType<DistanceDataMessage> DISTANCE_DATA =
            MessageType.fromEncoding(
                    Encodings.stack(
                            new ListEncoding<>(new IntegerEncoding(CONTEXT), 3),
                            Bijection.create(
                                    (DistanceDataMessage msg) -> Arrays.asList( msg.getX(), msg.getY(), msg.getZ() ),
                                    (List<Integer> ints) -> new DistanceDataMessage(ints.get(0), ints.get(1), ints.get(2))
                            )
                    )
            );

    /**
     * MessageType for {@link SpeedCmdMessage}. [char, byte] -> [direction, speed]
     */
    public static final MessageType<SpeedCmdMessage> SPEED_CMD =
            MessageType.fromEncoding(
                    Encodings.join(
                            new CharEncoding(CONTEXT),
                            new ByteEncoding(CONTEXT),
                            Bijection.create(
                                    (Tuple<Character, Byte> t) -> new SpeedCmdMessage(t.first, t.second),
                                    (SpeedCmdMessage msg) -> new Tuple<>(msg.getDirection(), msg.getSpeed())
                            )
                    )
            );

    /**
     * The MessageIdentifier used to identify Arduino Messages.
     */
    public static final MessageIdentifier<Character> IDENTIFIER =
            new SimpleMessageIdentifier<>(new CharEncoding(CONTEXT));

    /**
     * Message Type Registry for the arduino, where the identifiers for
     * the different message types are defined
     */
    public static final MessageTypeRegistry<Character> REGISTRY =
            Messaging.createRegistry(
                    IDENTIFIER,
                    (p) -> {
                        p.apply('d', ArduinoMessageTypes.DISTANCE_DATA);
                        p.apply('s', ArduinoMessageTypes.SPEED_CMD);
                    }
            );

    /**
     * Encoding for the {@link ArduinoMessageTypes#REGISTRY}
     */
    public static final Encoding<Message> ENCODING = REGISTRY;

}
