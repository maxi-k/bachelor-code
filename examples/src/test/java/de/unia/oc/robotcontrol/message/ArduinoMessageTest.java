/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArduinoMessageTest {

    @Test
    void speedCommandMessageWorks() {
        char direction = 's';
        byte speed = (byte) (Math.random() * 255);

        SpeedCmdMessage m = new SpeedCmdMessage(direction, speed);

        Assertions.assertEquals(
                ArduinoMessageTypes.SPEED_CMD.decode(m.getType().encode(m)),
                m
        );

        Assertions.assertEquals(
                ArduinoMessageTypes.ENCODING.decode(
                        ArduinoMessageTypes.ENCODING.encode(m)),
                m
        );
    }

    @Test
    void distanceDataMessageWorks() {
        DistanceDataMessage m = new DistanceDataMessage(
                (int) (Math.random() * 255),
                (int) (Math.random() * 1023),
                (int) (Math.random() * 2047)
        );

        Assertions.assertEquals(
                ArduinoMessageTypes.DISTANCE_DATA.decode(m.getType().encode(m)),
                m
        );

        Assertions.assertEquals(
                ArduinoMessageTypes.DISTANCE_DATA.decode(
                        ArduinoMessageTypes.DISTANCE_DATA.encode(m)),
                m
        );
    }

    @Test
    void updateRequestMessageWorkds() {

        UpdateRequestMessage m = new UpdateRequestMessage();

        Assertions.assertEquals(
                ArduinoMessageTypes.UPDATE_REQUEST.decode(m.getType().encode(m)),
                m
        );

        Assertions.assertEquals(
                ArduinoMessageTypes.ENCODING.decode(
                        ArduinoMessageTypes.ENCODING.encode(m)),
                m
        );
    }
}
