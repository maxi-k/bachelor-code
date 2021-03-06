package de.unia.oc.robotcontrol.message;

import org.reactivestreams.Subscriber;
import reactor.core.publisher.BaseSubscriber;

import java.util.function.Consumer;

/**
 * An implementation of {@link MessageRecipient} used to execute
 * a callback when a message is received.
 * @param <T> the type of value this (and the callback) can receive
 */
public class CallbackMessageRecipient<T extends Message> implements MessageRecipient<T> {

    private final Subscriber<T> subscriber;

    public CallbackMessageRecipient(Consumer<? super T> callback) {
        this.subscriber = new BaseSubscriber<T>() {
            @Override
            protected void hookOnNext(T value) {
                callback.accept(value);
            }
        };
    }

    @Override
    public Subscriber<T> asSubscriber() {
        return this.subscriber;
    }
}
