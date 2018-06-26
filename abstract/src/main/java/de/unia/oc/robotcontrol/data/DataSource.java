/* 2016 */
package de.unia.oc.robotcontrol.data;

import de.unia.oc.robotcontrol.util.Feed;

/**
 * A generic interface that is a source of data as wrapped
 * by {@link DataPayload}.
 *
 * @param <T>
 */
public interface DataSource<T extends DataPayload> extends Feed<T> {

}
