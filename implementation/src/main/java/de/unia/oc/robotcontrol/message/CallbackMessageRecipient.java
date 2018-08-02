package de.unia.oc.robotcontrol.message;

import org.reactivestreams.Subscriber;
import reactor.core.publisher.BaseSubscriber;

import java.util.function.Consumer;

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
