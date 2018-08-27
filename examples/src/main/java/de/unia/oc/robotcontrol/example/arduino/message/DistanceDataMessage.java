/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.message;

import de.unia.oc.robotcontrol.message.AbstractDeviceMessage;
import de.unia.oc.robotcontrol.message.MessageType;
import de.unia.oc.robotcontrol.message.SensorMessage;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A message wrapping the distance data from the
 * three arduino ultrasound sensors (front, y, z).
 */
public class DistanceDataMessage
        extends AbstractDeviceMessage<DistanceDataMessage>
        implements SensorMessage<DistanceDataMessage> {

    private final long time;

    private final @NonNegative int front, right, left; // front, right, left

    public DistanceDataMessage(@NonNegative int front, @NonNegative int right, @NonNegative int left) {
        this.time = System.currentTimeMillis();
        this.front = front;
        this.right = right;
        this.left = left;
    }

    @Override
    public MessageType<DistanceDataMessage> getType() {
        return ArduinoMessageTypes.DISTANCE_DATA;
    }

    public int getFront() {
        return front;
    }

    public int getRight() {
        return right;
    }

    public int getLeft() {
        return left;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof DistanceDataMessage)) {
            return false;
        }
        DistanceDataMessage msg = (DistanceDataMessage) obj;
        return msg == this
                || super.equals(msg)
                && msg.front == this.front
                && msg.right == this.right
                && msg.left == this.left;
    }

    @Override
    public String toString() {
        return "DistanceData Message: front: " + front + ", right: " + right + ", left: " + left;
    }

    @Override
    public long getCreationTime() {
        return this.time;
    }
}
