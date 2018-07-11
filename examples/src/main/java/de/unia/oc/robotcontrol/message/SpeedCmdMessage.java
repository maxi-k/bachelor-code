/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

/**
 * A message wrapping the command sent to an arduino used for
 * setting its speed.
 *
 * TODO: Currently simplified to only include one direction and speed parameter
 */
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpeedCmdMessage)) {
            return false;
        }
        SpeedCmdMessage msg = (SpeedCmdMessage) obj;
        return msg.direction == this.direction &&
                msg.speed == this.speed;
    }

    @Override
    public String toString() {
        return "SpeedCmd Message: dir: " + direction + " speed: " + speed;
    }
}
