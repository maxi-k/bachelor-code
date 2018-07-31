/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.EmittingMulticast;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.LatestFlowStrategy;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

public class EmittingMessageMulticast
    extends EmittingMulticast<MessageType, Message>
        implements MessageMulticaster {

    private final FlowStrategy<Message, Message> flowStrategy = LatestFlowStrategy.create();

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
    protected MessageType topicFromValue(Message message) {
        return message.getType();
    }

    @Override
    public FlowStrategy<Message, Message> getFlowStrategy() {
        return flowStrategy;
    }
}
