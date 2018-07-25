/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.message.DistanceDataMessage;

public class DistanceSensor extends IdentitySensor<DistanceDataMessage, DistanceDataMessage> {

    public DistanceSensor(DistanceDataMessage initialValue) {
        super(initialValue);
    }

}
