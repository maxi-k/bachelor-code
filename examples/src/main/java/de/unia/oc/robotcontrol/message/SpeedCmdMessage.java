/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.data.RobotDrivingCommand;

/**
 * A message wrapping the command sent to an arduino used for
 * setting its speed.
 *
 * TODO: Currently simplified to only include one command and speed parameter
 */
public class SpeedCmdMessage implements Message<SpeedCmdMessage> {

    private final RobotDrivingCommand command;
    private final int speed;

    public SpeedCmdMessage(char command, int speed) {
        this.command = RobotDrivingCommand.fromIdentifier(command);
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
    public boolean equals(Object obj) {
        if (!(obj instanceof SpeedCmdMessage)) {
            return false;
        }
        SpeedCmdMessage msg = (SpeedCmdMessage) obj;
        return msg.command == this.command &&
                msg.speed == this.speed;
    }

    @Override
    public String toString() {
        return "SpeedCmd Message: dir: " + command + " speed: " + speed;
    }
}
