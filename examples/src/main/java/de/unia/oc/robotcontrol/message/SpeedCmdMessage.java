/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

public class SpeedCmdMessage implements Message<SpeedCmdMessage> {

    private final char direction;
    private final byte speed;

    public SpeedCmdMessage(char direction, byte speed) {
        this.direction = direction;
        this.speed = speed;
    }

    public char getDirection() {
        return direction;
    }

    public byte getSpeed() {
        return speed;
    }

    @Override
    public MessageType<SpeedCmdMessage> getType() {
        return ArduinoMessageTypes.SPEED_CMD;
    }
}
