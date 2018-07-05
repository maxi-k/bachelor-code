/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;


import de.unia.oc.robotcontrol.util.Subscriber;

public interface MessageRecipient<T extends Message> extends Subscriber<T> {
}
