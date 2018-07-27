/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.message;

import de.unia.oc.robotcontrol.message.ActuatorMessage;
import de.unia.oc.robotcontrol.message.MessageType;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Message with no content that just requests a state update
 * from the Arduino.
 */
public class UpdateRequestMessage implements ActuatorMessage<UpdateRequestMessage> {

    private final long time;

    public UpdateRequestMessage() {
        this.time = System.currentTimeMillis();
    }

    @Override
    public MessageType<UpdateRequestMessage> getType() {
        return ArduinoMessageTypes.UPDATE_REQUEST;
    }

    @Override
    public long getCreationTime() {
        return time;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof UpdateRequestMessage;
    }

    @Override
    public String toString() {
        return "Update Request Message :" + super.toString();
    }
}
