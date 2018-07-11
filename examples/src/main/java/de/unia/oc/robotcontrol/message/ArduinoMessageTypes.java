package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.*;
import de.unia.oc.robotcontrol.util.Bijection;
import de.unia.oc.robotcontrol.util.Tuple;

import java.util.Arrays;
import java.util.List;

public final class ArduinoMessageTypes {

    private ArduinoMessageTypes() {}

    public static final CodingContext CONTEXT = CodingContext.ARDUINO;

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

    public static final MessageIdentifier<Character> IDENTIFIER =
            new CharIdentifier(CONTEXT);

    public static final MessageTypeRegistry<Character> REGISTRY =
            Messaging.createRegistry(
                    IDENTIFIER,
                    (p) -> {
                        p.apply('d', ArduinoMessageTypes.DISTANCE_DATA);
                        p.apply('s', ArduinoMessageTypes.SPEED_CMD);
                    }
            );

    public static final Encoding<Message> ENCODING = REGISTRY;

}