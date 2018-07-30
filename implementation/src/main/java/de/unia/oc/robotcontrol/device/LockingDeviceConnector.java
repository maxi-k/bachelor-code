/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.message.Message;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public abstract class LockingDeviceConnector<Input extends Message<Input>,
        Output extends Message<Output>>
        implements Device<Input, Output> {

    private final Queue<Input> messageQueue;

    protected final Encoding<Input> inputEncoding;
    protected final Encoding<Output> outputEncoding;

    protected final Supplier<Input> updateRequestMessageProvider;

    private final Object deviceLock;

    private @MonotonicNonNull Subscription subscription;
    private final Flux<Output> publisher;
    private final BaseSubscriber<Input> subscriber;

    @SuppressWarnings("initialization")
    public LockingDeviceConnector(
            Encoding<Input> inputEncoding,
            Encoding<Output> outputEncoding,
            Supplier<Input> updateRequestMessageProvider) {
        this.inputEncoding = inputEncoding;
        this.outputEncoding = outputEncoding;
        this.deviceLock = new Object();

        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.updateRequestMessageProvider = updateRequestMessageProvider;

        this.subscriber = new BaseSubscriber<Input>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                LockingDeviceConnector.this.subscription = subscription;
                requestUnbounded();
            }

            @Override
            protected void hookOnNext(Input value) {
                LockingDeviceConnector.this.queueMessage(value);
            }
        };

        this.publisher = Flux.push(
                (FluxSink<Output> next) -> {
                    if (this.subscription != null) {
                        next.next(getAnswer());
                    }
                },
                FluxSink.OverflowStrategy.BUFFER
        ).publishOn(Schedulers.elastic());

    }

    @Override
    public Publisher<Output> asPublisher() {
        return publisher;
    }

    @Override
    public Subscriber<Input> asSubscriber() {
        return subscriber;
    }

    private void queueMessage(Input m) {
        synchronized(this.messageQueue) {
            this.messageQueue.add(m);
        }
    }

    private Output getAnswer() {
        synchronized (deviceLock) {
            try {
                pushMessage(this.inputEncoding.encode(getNext()));
                Thread.sleep(10);
                return this.outputEncoding.decode(retrieveMessage());
            } catch (InterruptedException | IOException e) {
                System.err.println("Error while sending or retrieving message!");
                e.printStackTrace();
                throw new RuntimeException(e);
                // return new ErrorMessage(e);
            } catch (Exception e) {
                System.err.println("Unexpected error while sending or retrieving message! ");
                e.printStackTrace();
                throw e;
                // return new ErrorMessage(e);
            }
        }
    }

    private Input getNext() {
        synchronized (this.messageQueue) {
            return this.messageQueue.isEmpty()
                    ? this.updateRequestMessageProvider.get()
                    : this.messageQueue.remove();
        }
    }

    protected abstract void pushMessage(byte[] message) throws IOException;

    protected abstract byte[] retrieveMessage() throws IOException;
}
