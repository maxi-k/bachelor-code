package de.unia.oc.robotcontrol.message;

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
}
