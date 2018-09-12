/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

/**
 * A recipient of data that only allows for certain types
 * of elements to be received.
 *
 * @param <T> the type of object this is allowed to receive
 */
public interface SelectiveRecipient<T> {

    /**
     * The class of element this is allowed to receive.
     * If any type is allowed to be received, this should
     * return {@code Object.class}
     * @return an instance of {@link Class}
     */
    Class<T> getAcceptedClass();
}
