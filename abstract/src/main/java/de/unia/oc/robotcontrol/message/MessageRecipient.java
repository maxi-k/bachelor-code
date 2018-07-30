/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;


import de.unia.oc.robotcontrol.flow.SubscriberProvider;

/**
 * Generic interface for some element that can receive Messages
 * @param <T>
 */
public interface MessageRecipient<T extends Message<T>> extends SubscriberProvider<T> {

}
