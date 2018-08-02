/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.EmittingMulticast;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.LatestFlowStrategy;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

public class EmittingMessageMulticast<T extends Message>
    extends EmittingMulticast<MessageType<? extends T>, T>
        implements MessageMulticast<T> {

    private final FlowStrategy<T, T> flowStrategy = LatestFlowStrategy.create();

    public EmittingMessageMulticast(Scheduler scheduler) {
        super(scheduler);
    }

    public EmittingMessageMulticast(Executor executor) {
        super(executor);
    }

    public EmittingMessageMulticast() {
        super(Schedulers.newParallel("parallelMessageMulticaster"));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MessageType topicFromValue(T message) {
        return message.getType();
    }

    @Override
    public FlowStrategy<T, T> getFlowStrategy() {
        return flowStrategy;
    }
}
