/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.data;

import de.unia.oc.robotcontrol.flow.InFlowElement;

/**
 * A generic interface for something that receives data
 * as wrapped by {@link DataPayload}.
 *
 * @param <T>
 */
public interface DataSink<T extends DataPayload> extends InFlowElement {

}
