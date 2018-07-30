/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.old.ActiveOutFlow;
import de.unia.oc.robotcontrol.flow.old.PassiveInFlow;
import de.unia.oc.robotcontrol.util.BidirectionalRegistry;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

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

    private final Executor executor;
    // private final Map<Consumer<T>, Queue<T>> dispatchQueue;

    private final Flux<T> publisher;
    private final Subscriber<T> subscriber;


    @SuppressWarnings("initialization")
    public QueuedMessageDispatcher(Executor executor) {
        this.executor = executor;
        // this.dispatchQueue = new HashMap<>();
        // this.inFlow = InFlows.createUnbuffered(this::dispatch); //passive
        // this.outFlow = OutFlows.createOnDemand(this.executor, this::popNextItemFor); //active
        this.publisher = Flux.push((emitter) -> {

        })
                .switchMap()
                .publishOn(Schedulers.elastic());
    }

    public QueuedMessageDispatcher() {
        this(Executors.newCachedThreadPool());
    }

    @Override
    // TODO: Figure out a way to generify
    public void dispatch(T msg) throws ItemNotRegisteredException {
        MessageType<T> type = msg.getType();
        MessageRecipient<T> recp = getValueFor(type).orElseThrow(ItemNotRegisteredException::new);
        PassiveInFlow<T> inFlow = (PassiveInFlow<T>) recp.inFlow();

        this.getQueueFor(inFlow).add(msg);
        this.outFlow().get().accept(inFlow);
        this.publisher.
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

    private @Nullable T popNextItemFor(Consumer<T> recipient) {
        try {
            return this.getQueueFor(recipient).remove();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private Queue<T> createQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Override
    public Publisher<T> asPublisher() {
        return null;
    }

    @Override
    public Subscriber<T> asSubscriber() {
        return null;
    }
}
