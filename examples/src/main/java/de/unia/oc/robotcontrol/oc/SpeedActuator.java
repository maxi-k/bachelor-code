/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.message.SpeedCmdMessage;

public class SpeedActuator extends IdentityActuator<SpeedCmdMessage, SpeedCmdMessage> {

    public SpeedActuator(SpeedCmdMessage initialValue) {
        super(initialValue);
    }
}
