/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import java.util.Arrays;
import java.util.List;

public class SpeedCmdMessage implements Message<SpeedCmdMessage> {

    public final boolean isCmd;
    public final int[] speeds;
    public final char command;

    public SpeedCmdMessage(char command) {
        this.isCmd = true;
        this.command = command;
        this.speeds = null;
    }

    public SpeedCmdMessage(int[] speeds) {
        this.isCmd = false;
        this.speeds = speeds;
        this.command = Character.SPACE_SEPARATOR;
    }

    public SpeedCmdMessage(List<Integer> speeds) {
        this.isCmd  = false;
        this.command = Character.SPACE_SEPARATOR;
        this.speeds = new int[speeds.size()];
        for (int i = 0; i < speeds.size(); ++i) {
            this.speeds[i] = speeds.get(i);
        }
    }

    @Override
    public MessageType<SpeedCmdMessage> getType() {
        return ArduinoMessageType.SpeedCommand.instance();
    }

    @Override
    public String toString() {
        if (isCmd) {
            return "SpeedCommand: " + command;
        } else {
            return "SpeedValues: " + Arrays.toString(speeds);
        }
    }
}
