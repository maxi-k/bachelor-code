/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

/**
 * A message wrapping the distance data from the
 * three arduino ultrasound sensors (x, y, z).
 */
public class DistanceDataMessage implements Message<DistanceDataMessage> {

    private final int x;
    private final int y;
    private final int z;

    public DistanceDataMessage(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public MessageType<DistanceDataMessage> getType() {
        return ArduinoMessageTypes.DISTANCE_DATA;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DistanceDataMessage)) {
            return false;
        }
        DistanceDataMessage msg = (DistanceDataMessage) obj;
        return msg.x == this.x
                && msg.y == this.y
                && msg.z == this.z;
    }

    @Override
    public String toString() {
        return "DistanceData Message: x: " + x + ", y: " + y + ", z: " + z;
    }
}
