/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.Schedulable;

import java.util.function.Supplier;

/**
 * Describes something that can somehow provider items of type {@link T}
 *
 * @param <T> The type of items this can provide
 */
public interface OutFlow<T> extends Supplier<T>, FlowPressure, Schedulable {

}