/* 2016 */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.util.Registry;

public interface MessageTypeRegistry<I> extends Registry<I, MessageType> {

    MessageIdentifier<I> getIdentifier();

}
