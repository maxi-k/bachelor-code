/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.message;

import de.unia.oc.robotcontrol.message.AbstractActuatorMessage;
import de.unia.oc.robotcontrol.message.MessageType;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

/**
 * Message with no content that just requests a state update
 * from the Arduino.
 */
public class UpdateRequestMessage extends AbstractActuatorMessage<UpdateRequestMessage> {

    private volatile @MonotonicNonNull UUID deviceUUID;

    public UpdateRequestMessage() {
    }

    @Override
    public MessageType<UpdateRequestMessage> getType() {
        return ArduinoMessageTypes.UPDATE_REQUEST;
    }

   @Override
    public boolean equals(@Nullable Object obj) {
        return obj == this
                || obj instanceof UpdateRequestMessage
                && super.equals(obj);
    }

    @Override
    public String toString() {
        return "Update Request Message :" + super.toString();
    }
}
