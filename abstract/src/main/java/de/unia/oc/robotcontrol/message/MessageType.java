/* 2016 */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.Encoding;

/**
 * An interface that defines the type of a message by
 * providing an {@link Encoding} for it.
 *
 * @param <T>
 */
public interface MessageType<T extends Message> extends Encoding<T> {
}
