/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

/**
 * Will probably be removed or changed.
 * Not in use
 */
public interface FlowItem<T> {

    T get() throws NoValuePresentException;

    Exception getError();

    boolean isComplete();

    default boolean hasError() {
        return getError() == null;
    }

    class NoValuePresentException extends RuntimeException {}
}
