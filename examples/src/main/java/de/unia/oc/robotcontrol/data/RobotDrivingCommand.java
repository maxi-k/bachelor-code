/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.data;

public enum RobotDrivingCommand {

    FRONT('w'),
    LEFT('a'),
    RIGHT('d'),
    STOP('s'),
    ROTATE('r');


    private final char identifier;

    RobotDrivingCommand(char identifier) {
        this.identifier = identifier;
    }

    public char getIdentifier() {
        return identifier;
    }

    public static RobotDrivingCommand fromIdentifier(char id) {
        for (RobotDrivingCommand cmd : values()) {
            if (cmd.identifier == id)
                return cmd;
        }
        return null;
    }

    public static boolean isIdentifier(char id) {
        return fromIdentifier(id) != null;
    }
}
