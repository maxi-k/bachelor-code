/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.Schedulable;

import java.util.function.Supplier;

interface OutFlow<T> extends Supplier<T>, FlowPressure, Schedulable {

}