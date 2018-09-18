/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;

import java.util.function.Consumer;

/**
 * Implementation of {@link FlowStrategy} used for printing values
 * using {@link System#out} when items traverse the transformed publisher
 *
 * @param <T> the type of object published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class PrinterFlowStrategy<T extends Object> extends CallbackFlowStrategy<T> {

    /**
     * Create a new {@link PrinterFlowStrategy} by passing
     * {@code System.out::println} as a callback to
     * {@link CallbackFlowStrategy#CallbackFlowStrategy(Consumer)}
     */
    private PrinterFlowStrategy() {
        super(System.out::println);
    }

    /**
     * Create a new instance of {@link PrinterFlowStrategy}.
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link PrinterFlowStrategy}
     */
    public static <T> PrinterFlowStrategy<T> create() {
        return new PrinterFlowStrategy<>();
    }

}
