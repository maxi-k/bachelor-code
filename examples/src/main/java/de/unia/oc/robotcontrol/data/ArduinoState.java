/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.data;

import de.unia.oc.robotcontrol.message.DistanceDataMessage;
import de.unia.oc.robotcontrol.util.CollectionUtil;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public class ArduinoState {

    private @MonotonicNonNull DistanceDataMessage distances;
    private @MonotonicNonNull DistanceDataMessage prevDistances;

    private ArduinoState() {

    }

    public <D> void updateWith(DataPayload<D> data) {
        if (data instanceof DistanceDataMessage) {
            updateDistances((DistanceDataMessage) data);
        }
    }

    private synchronized void updateDistances(DistanceDataMessage data) {
        this.prevDistances = this.distances;
        this.distances = data;
    }

    public String encode() {
        return CollectionUtil.joinWith(";",
                String.valueOf(distances.getFront()),
                String.valueOf(distances.getRight()),
                String.valueOf(distances.getLeft()),

                String.valueOf(prevDistances.getFront()),
                String.valueOf(prevDistances.getRight()),
                String.valueOf(prevDistances.getLeft())
        );
    }

    public static ArduinoState createEmpty() {
        return new ArduinoState();
    }
}
