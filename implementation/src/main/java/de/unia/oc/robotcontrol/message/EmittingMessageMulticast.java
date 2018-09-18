/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.ReemittingMulticast;
import de.unia.oc.robotcontrol.flow.strategy.LatestFlowStrategy;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

/**
 * Message multicast following the same semantics as {@link ReemittingMulticast}.
 * The only notable difference is the {@link #topicFromValue(Message)} function,
 * which can be implemented using {@link Message#getType()}.
 * @param <T> the subtype of Message this processes
 */
public class EmittingMessageMulticast<T extends Message>
    extends ReemittingMulticast<MessageType<? extends T>, T>
        implements MessageMulticast<T> {

    private final FlowStrategy<T, T> flowStrategy = LatestFlowStrategy.create();

    public EmittingMessageMulticast(Scheduler scheduler) {
        super(scheduler);
    }

    public EmittingMessageMulticast(Executor executor) {
        super(executor);
    }

    public EmittingMessageMulticast() {
        super(Schedulers.newParallel("parallelMessageMulticast"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public MessageType topicFromValue(T message) {
        return message.getType();
    }

    @Override
    public FlowStrategy<T, T> getFlowStrategy() {
        return flowStrategy;
    }
}
