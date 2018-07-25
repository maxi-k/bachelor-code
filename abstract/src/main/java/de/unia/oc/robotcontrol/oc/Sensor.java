/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.data.DataPayload;
import de.unia.oc.robotcontrol.data.DataSource;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.message.MessageRecipient;

public interface Sensor<D extends DataPayload, M extends Message<M>>
        extends DataSource<D>, MessageRecipient<M> {
}
