/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * Implementation of {@link FlowStrategy} used for filtering the
 * types of values that continue down the line using their {@link Class}.
 * By consequence, for an input element (of type {@link Input}) to pass,
 * the class of {@link Output} must be one of its super-classes.
 *
 * @param <Input> the type of object published by the Publisher
 *                which is to be transformed by this flow strategy
 * @param <Output> the type of object published by the transformed Publisher,
 *                filtered by their {@link Class}
 */
public class TypeFilterFlowStrategy<Input extends Object, Output extends Object>
        implements FlowStrategy<Input, Output> {

    /**
     * The class to filter objects by using {@link Class#isInstance(Object)}
     */
    private final Class<Output> outputCls;

    /**
     * Create a new instance of {@link TypeFilterFlowStrategy} which filters
     * the passed values based on if they are instances of the given class
     * @param outputCls the class to filter by using {@link Class#isInstance(Object)}
     */
    private TypeFilterFlowStrategy(Class<Output> outputCls) {
        this.outputCls = outputCls;
    }

    /**
     *
     * Create a new instance of {@link TypeFilterFlowStrategy} which filters
     * the passed values based on if they are instances of the given class
     * @param outputCls the class to filter by using {@link Class#isInstance(Object)}
     * @param <Input> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @param <Output> the type of object published by the transformed Publisher
     * @return a new isntance of {@link TypeFilterFlowStrategy}
     */
    public static <Input, Output> TypeFilterFlowStrategy<Input, Output> create(Class<Output> outputCls) {
        return new TypeFilterFlowStrategy<>(outputCls);
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.TRANSPARENT;
    }

    @Override
    public Publisher<Output> apply(Publisher<Input> inputPublisher) {
        final Object unique = new Object();
        return Flux.from(inputPublisher)
                .map((i) -> outputCls.isInstance(i) ? i : unique)
                .filter((i) -> i != unique)
                .cast(outputCls);
    }
}
