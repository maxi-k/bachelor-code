/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.message;

import de.unia.oc.robotcontrol.example.arduino.data.RobotDrivingCommand;
import de.unia.oc.robotcontrol.message.AbstractActuatorMessage;
import de.unia.oc.robotcontrol.message.MessageType;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A message wrapping the command sent to an arduino used for
 * setting its speed.
 */
public class SpeedCmdMessage extends AbstractActuatorMessage<SpeedCmdMessage> {

    private final RobotDrivingCommand command;
    private final int speed;

    public SpeedCmdMessage(char command, int speed) {
        this.command = RobotDrivingCommand.fromIdentifier(command).orElseThrow(IllegalArgumentException::new);
        this.speed = speed;
    }

    public SpeedCmdMessage(RobotDrivingCommand command, int speed) {
        this.command = command;
        this.speed = speed;
    }

    public RobotDrivingCommand getCommand() {
        return command;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public MessageType<SpeedCmdMessage> getType() {
        return ArduinoMessageTypes.SPEED_CMD;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof SpeedCmdMessage)) {
            return false;
        }
        SpeedCmdMessage msg = (SpeedCmdMessage) obj;
        return obj == this
                || super.equals(msg)
                && msg.command == this.command
                && msg.speed == this.speed;
    }

    @Override
    public String toString() {
        return "SpeedCmd Message: dir: " + command + " speed: " + speed;
    }

}
