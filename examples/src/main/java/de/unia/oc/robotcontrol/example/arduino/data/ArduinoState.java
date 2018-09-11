/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.data;

import de.unia.oc.robotcontrol.example.arduino.message.DistanceDataMessage;
import de.unia.oc.robotcontrol.message.SensorMessage;
import de.unia.oc.robotcontrol.util.CollectionUtil;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

/**
 * The WorldState as required by {@link de.unia.oc.robotcontrol.oc.Observer} and
 * {@link de.unia.oc.robotcontrol.oc.Controller} for the arduino.
 */
public class ArduinoState {

    /**
     * The distances that were last measured
     */
    private @MonotonicNonNull DistanceDataMessage distances;
    /**
     * The distances that were measured before the last measurement.
     */
    private @MonotonicNonNull DistanceDataMessage prevDistances;

    private ArduinoState() { }

    /**
     * Update this Object using the passed message
     * @param data the message to update with
     */
    public void updateWith(SensorMessage data) {
        if (data instanceof DistanceDataMessage) {
            updateDistances((DistanceDataMessage) data);
        }
    }

    /**
     * Set recorded distances
     * @param data The {@link DistanceDataMessage} encapsulating the measured distances
     */
    private synchronized void updateDistances(DistanceDataMessage data) {
        this.prevDistances = this.distances == null ? data : this.distances;
        this.distances = data;
    }

    /**
     * Encode this State as a string,
     * possibly used by learning algorithms
     * @return a string representation of the relevant state
     */
    public String encode() {
        if (distances == null || prevDistances == null) {
            return "";
        }
        return encodeNewest();
    }

    /**
     * Encode only the newest recorded distances as a string.
     * @return a string containing the distances
     */
    @RequiresNonNull({"distances"})
    private String encodeNewest() {
        return CollectionUtil.joinWith(";",
                String.valueOf(distances.getFront()),
                String.valueOf(distances.getRight()),
                String.valueOf(distances.getLeft())
        );
    }

    /**
     * Encode both the newest and previously recorded distances
     * as a string.
     * @return a string representation of
     */
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

    /**
     * Create a new instance of {@link ArduinoState} without
     * any recorded data
     * @return a new instance of {@link ArduinoState}
     */
    public static ArduinoState createEmpty() {
        return new ArduinoState();
    }
}
