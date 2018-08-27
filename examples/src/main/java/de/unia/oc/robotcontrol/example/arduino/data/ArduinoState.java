/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.data;

import de.unia.oc.robotcontrol.example.arduino.message.DistanceDataMessage;
import de.unia.oc.robotcontrol.message.SensorMessage;
import de.unia.oc.robotcontrol.util.CollectionUtil;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

public class ArduinoState {

    private @MonotonicNonNull DistanceDataMessage distances;
    private @MonotonicNonNull DistanceDataMessage prevDistances;

    private ArduinoState() {

    }

    public void updateWith(SensorMessage data) {
        if (data instanceof DistanceDataMessage) {
            updateDistances((DistanceDataMessage) data);
        }
    }

    private synchronized void updateDistances(DistanceDataMessage data) {
        this.prevDistances = this.distances == null ? data : this.distances;
        this.distances = data;
    }

    public String encode() {
        if (distances == null || prevDistances == null) {
            return "";
        }
        return encodeNewest();
    }

    @RequiresNonNull({"distances"})
    private String encodeNewest() {
        return CollectionUtil.joinWith(";",
                String.valueOf(distances.getFront()),
                String.valueOf(distances.getRight()),
                String.valueOf(distances.getLeft())
        );
    }

    @RequiresNonNull({"distances", "prevDistances"})
    private String encodeWithPrevious() {
        return CollectionUtil.joinWith(";",
                String.valueOf(distances.getFront()),
                String.valueOf(distances.getRight()),
                String.valueOf(distances.getLeft()),

                String.valueOf(prevDistances.getFront()),
                String.valueOf(prevDistances.getRight()),
                String.valueOf(prevDistances.getLeft())
        );
    }

    public int getFrontDist() {
        return distances == null ? 0 : distances.getFront();
    }

    public int getPrevFrontDist() {
        return prevDistances == null ? 0 : prevDistances.getFront();
    }

    public int getRightDist() {
        return distances == null ? 0 : distances.getRight();
    }

    public int getPrevRightDist() {
        return prevDistances == null ? 0 : prevDistances.getRight();
    }

    public int getLeftDist() {
        return distances == null ? 0 : distances.getLeft();
    }

    public int getPrevLeftDist() {
        return prevDistances == null ? 0 : prevDistances.getLeft();
    }

    public static ArduinoState createEmpty() {
        return new ArduinoState();
    }
}
