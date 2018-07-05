/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;


import java.util.function.Consumer;

interface InFlow<T> extends Consumer<T>, FlowPressure {

}
