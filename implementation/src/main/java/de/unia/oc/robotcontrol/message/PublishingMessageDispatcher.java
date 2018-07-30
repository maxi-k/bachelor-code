/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.old.PassiveInFlow;
import de.unia.oc.robotcontrol.util.MultiBiRegistry;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PublishingMessageDispatcher<T extends Message<T>>
        extends MultiBiRegistry<MessageType<T>, MessageRecipient<T>>
        implements MessageDispatcher<T> {

    private final Executor executor;
    private final Map<Consumer<T>, > publisherMap;

    private final Flux<T> publisher;
    private final Subscriber<T> subscriber;


    @SuppressWarnings("initialization")
    public PublishingMessageDispatcher(Executor executor) {
        this.executor = executor;
        // this.dispatchQueue = new HashMap<>();
        // this.inFlow = InFlows.createUnbuffered(this::dispatch); //passive
        // this.outFlow = OutFlows.createOnDemand(this.executor, this::popNextItemFor); //active
        this.publisher = Flux.push((emitter) -> {

        })
                .publishOn(Schedulers.fromExecutor(executor));
    }

    public PublishingMessageDispatcher() {
        this(Executors.newCachedThreadPool());
    }

    @Override
    // TODO: Figure out a way to generify
    public void dispatch(T msg) throws IllegalArgumentException {
        MessageType<T> type = msg.getType();
        MessageRecipient<T> recp = getValueFor(type).orElseThrow(IllegalArgumentException::new);
        PassiveInFlow<T> inFlow = (PassiveInFlow<T>) recp.inFlow();

        Stream<Message> s = null;

        this.getQueueFor(inFlow).add(msg);
        this.outFlow().get().accept(inFlow);
        this.publisher.
    }

    private @Nullable T popNextItemFor(Consumer<T> recipient) {
        try {
            return this.getQueueFor(recipient).remove();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public boolean register(MessageType<T> key, MessageRecipient<T> value) {
         boolean result = super.register(key, value);
         return result;
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
