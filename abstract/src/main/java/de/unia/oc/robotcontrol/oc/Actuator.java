/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.data.DataPayload;
import de.unia.oc.robotcontrol.data.DataSink;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.message.MessageSender;

public interface Actuator<D extends DataPayload, M extends Message<M>>
        extends DataSink<D>, MessageSender<M> {

}
