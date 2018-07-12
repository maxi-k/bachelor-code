/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.ActiveOutFlow;
import de.unia.oc.robotcontrol.flow.InFlows;
import de.unia.oc.robotcontrol.flow.OutFlows;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.util.BidirectionalRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class QueuedMessageDispatcher<T extends Message<T>>
        extends BidirectionalRegistry<MessageType<T>, MessageRecipient<T>>
        implements MessageDispatcher<T> {

    private final PassiveInFlow<T> inFlow;
    private final ActiveOutFlow<T> outFlow;

    private final Executor executor;
    private final Map<Consumer<T>, Queue<T>> dispatchQueue;

    public QueuedMessageDispatcher(Executor executor) {
        this.executor = executor;
        this.dispatchQueue = new HashMap<>();
        this.inFlow = InFlows.createUnbuffered(this::dispatch);
        this.outFlow = OutFlows.createOnDemand(this.executor, this::popNextItemFor);
    }

    public QueuedMessageDispatcher() {
        this(Executors.newCachedThreadPool());
    }

    @Override
    public void dispatch(T msg) throws ItemNotRegisteredException {
        MessageType<T> type = msg.getType();
        MessageRecipient<T> recp = getValueFor(type).orElseThrow(ItemNotRegisteredException::new);
        PassiveInFlow<T> inFlow = (PassiveInFlow<T>) recp.inFlow();

        this.getQueueFor(inFlow).add(msg);
        this.outFlow().get().accept(inFlow);
    }

    @Override
    public PassiveInFlow<T> inFlow() {
        return inFlow;
    }

    @Override
    public ActiveOutFlow<T> outFlow() {
        return outFlow;
    }

    private Queue<T> getQueueFor(Consumer<T> recipient) {
        synchronized(this.dispatchQueue) {
            Queue<T> messageQueue = this.dispatchQueue.get(recipient);
            if (!this.dispatchQueue.containsKey(recipient) || messageQueue == null) {
                messageQueue = createQueue();
                this.dispatchQueue.put(recipient, messageQueue);
                return messageQueue;
            }
            return messageQueue;
        }
    }

    private T popNextItemFor(Consumer<T> recipient) {
        try {
            return this.getQueueFor(recipient).remove();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private Queue<T> createQueue() {
        return new LinkedBlockingQueue<>();
    }
}