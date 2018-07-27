/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.old;

import de.unia.oc.robotcontrol.concurrent.Schedulable;

import java.util.function.Supplier;

/**
 * Describes something that can somehow provider items of type {@link T}
 *
 * @param <T> The type of items this can provide
 */
interface OutFlow<T> extends Supplier<T>, FlowPressure, Schedulable {

}