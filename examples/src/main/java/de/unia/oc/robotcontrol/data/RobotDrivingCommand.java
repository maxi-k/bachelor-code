/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.data;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

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

    public static Optional<@NonNull RobotDrivingCommand> fromIdentifier(char id) {
        for (RobotDrivingCommand cmd : values()) {
            if (cmd.identifier == id)
                return Optional.of(cmd);
        }
        return Optional.empty();
    }

    public static boolean isIdentifier(char id) {
        return fromIdentifier(id) != null;
    }
}
