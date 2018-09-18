/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;


import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

/**
 * Implementation of {@link FlowStrategy} used for switching the execution context
 * of a publisher, from the point where this strategy was applied downstream,
 * until the next execution context switch occures.
 *
 * @param <T> the type of object published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class SchedulingFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    /**
     * The execution context to run this on
     */
    private final Scheduler scheduler;

    /**
     * Create a new {@link SchedulingFlowStrategy} instance,
     * where the given scheduler is used as execution context.
     * @param scheduler the {@link Scheduler} to run this on,
     */
    private SchedulingFlowStrategy(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Create a new {@link SchedulingFlowStrategy} instance,
     * where the given scheduler is used as execution context.
     * Mirror of {@link #SchedulingFlowStrategy(Scheduler)}
     *
     * @param scheduler the {@link Scheduler} to run this on,
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link SchedulingFlowStrategy}
     */
    public static <T> SchedulingFlowStrategy<T> create(Scheduler scheduler) {
        return new SchedulingFlowStrategy<>(scheduler);
    }

    /**
     * Create a new {@link SchedulingFlowStrategy} instance,
     * where the given executor is used as execution context.
     * @param executor the {@link Executor} to run this on,
     *                 which will be wrapped in a {@link Scheduler}
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link SchedulingFlowStrategy}
     */
    public static <T> SchedulingFlowStrategy<T> create(Executor executor) {
        return new SchedulingFlowStrategy<>(Schedulers.fromExecutor(executor));
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.TRANSPARENT;
    }

    @Override
    public Publisher<T> apply(Publisher<T> inputPublisher) {
        return Flux
                .from(inputPublisher)
                .publishOn(scheduler);
    }

}
