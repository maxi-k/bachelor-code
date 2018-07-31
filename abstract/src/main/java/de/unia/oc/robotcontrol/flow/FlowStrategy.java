/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Publisher;

import java.util.function.Function;

public interface FlowStrategy<T, R> extends Function<Publisher<T>, Publisher<R>> {

    String PROPERTY_NAME = "flowStrategy";

    FlowStrategyType getType();
}
