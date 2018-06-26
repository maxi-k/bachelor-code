/* 2016 */
package de.unia.oc.robotcontrol.data;

import de.unia.oc.robotcontrol.util.Subscriber;

/**
 * A generic interface for something that receives data
 * as wrapped by {@link DataPayload}.
 *
 * @param <T>
 */
public interface DataSink<T extends DataPayload> extends Subscriber<T> {

}
