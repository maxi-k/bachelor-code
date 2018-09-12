/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.message;

import de.unia.oc.robotcontrol.coding.*;
import de.unia.oc.robotcontrol.message.*;
import de.unia.oc.robotcontrol.util.Bijection;
import de.unia.oc.robotcontrol.util.Tuple;

import java.util.*;

/**
 * Define all message types ({@link MessageType}) which can
 * possibly be sent or received by the connected Arduino
 * as static fields.
 */
public final class ArduinoMessageTypes {

    private ArduinoMessageTypes() {}

    /**
     * Holds the coding-context in which the arduino is run
     */
    public static final CodingContext CONTEXT = CodingContext.ARDUINO;

    /**
     * MessageType for {@link DistanceDataMessage}. {@code [int, int, int] -> [x, y, z]}
     */
    public static final MessageType<DistanceDataMessage> DISTANCE_DATA =
            MessageType.fromEncoding(
                    Encodings.stack(
                            new ListEncoding<>(new IntegerEncoding(CONTEXT), 3),
                            Bijection.create(
                                    (DistanceDataMessage msg) -> Arrays.asList( msg.getFront(), msg.getRight(), msg.getLeft() ),
                                    (List<Integer> ints) -> new DistanceDataMessage(ints.get(0), ints.get(1), ints.get(2))
                            )
                    )
            ).withName("DistanceData");

    /**
     * MessageType for {@link SpeedCmdMessage}. {@code (char, byte) -> (direction, speed)}
     */
    public static final MessageType<SpeedCmdMessage> SPEED_CMD =
            MessageType.fromEncoding(
                    Encodings.join(
                            new CharEncoding(CONTEXT),
                            new IntegerEncoding(CONTEXT),
                            Bijection.create(
                                    (SpeedCmdMessage msg) -> new Tuple<>(msg.getCommand().getIdentifier(), msg.getSpeed()),
                                    (Tuple<Character, Integer> t) -> new SpeedCmdMessage(t.first, t.second)
                            )
                    )
            ).withName("SpeedCommand");

    /**
     * Empty message for requesting an update from the arduino.
     * When sent over the wire, contains only the identifier defined
     * by {@link #REGISTRY}.
     */
    public static final MessageType<UpdateRequestMessage> UPDATE_REQUEST =
            MessageType.fromEncoding(
                    Encodings.nullEncoding(CONTEXT, UpdateRequestMessage::new)
            ).withName("UpdateRequest");


    /**
     * List of all basic Message Types sendable to the arduino
     */
    public static final Set<MessageType<? extends Message>> messageTypeList =
            Collections.unmodifiableSet(
                    new HashSet<>(
                            Arrays.asList(DISTANCE_DATA, SPEED_CMD, UPDATE_REQUEST)
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
                        p.apply('d', DISTANCE_DATA);
                        p.apply('s', SPEED_CMD);
                        p.apply('u', UPDATE_REQUEST);
                    }
            );

    /**
     * Encoding for the {@link ArduinoMessageTypes#REGISTRY}.
     * Is able to encode and decode all messages defined here.
     */
    public static final Encoding<Message> ENCODING = REGISTRY;

}
