/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Message with no content that just requests a state update
 * from the Arduino.
 */
public class UpdateRequestMessage implements Message<UpdateRequestMessage> {

    @Override
    public MessageType<UpdateRequestMessage> getType() {
        return ArduinoMessageTypes.UPDATE_REQUEST;
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
