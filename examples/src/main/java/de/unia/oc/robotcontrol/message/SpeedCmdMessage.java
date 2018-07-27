/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.data.DataPayload;
import de.unia.oc.robotcontrol.data.RobotDrivingCommand;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A message wrapping the command sent to an arduino used for
 * setting its speed.
 *
 * TODO: Currently simplified to only include one command and speed parameter
 */
public class SpeedCmdMessage implements Message<SpeedCmdMessage>, DataPayload<SpeedCmdMessage> {

    private final long creationTime;

    private final RobotDrivingCommand command;
    private final int speed;

    public SpeedCmdMessage(char command, int speed) {
        this.creationTime = System.currentTimeMillis();
        this.command = RobotDrivingCommand.fromIdentifier(command).orElseThrow(IllegalArgumentException::new);
        this.speed = speed;
    }

    public SpeedCmdMessage(RobotDrivingCommand command, int speed) {
        this.creationTime = System.currentTimeMillis();
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
        return msg.command == this.command &&
                msg.speed == this.speed;
    }

    @Override
    public String toString() {
        return "SpeedCmd Message: dir: " + command + " speed: " + speed;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public SpeedCmdMessage getData() {
        return this;
    }
}
