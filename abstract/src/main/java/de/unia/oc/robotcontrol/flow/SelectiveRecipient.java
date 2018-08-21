/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

public interface SelectiveRecipient<T> {

    Class<T> getAcceptedClass();
}
