/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;


import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

public class SchedulingFlowStrategy<T extends Object> implements FlowStrategy<T, T> {

    private final Scheduler scheduler;

    private SchedulingFlowStrategy(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public static <T> SchedulingFlowStrategy<T> create(Scheduler scheduler) {
        return new SchedulingFlowStrategy<>(scheduler);
    }

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
