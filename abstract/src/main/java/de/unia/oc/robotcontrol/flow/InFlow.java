/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.Schedulable;

import java.util.function.Consumer;

/**
 * Describes something that can somehow accept items of type {@link T}
 *
 * @param <T> The type of items this can accept
 */
public interface InFlow<T> extends Consumer<T>, FlowPressure, Schedulable {

}
