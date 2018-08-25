/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

public class CallbackFlowStrategy<T extends Object> extends TransparentFlowStrategy<T> {

    private Consumer<T> callback;

    protected CallbackFlowStrategy(Consumer<T> callback) {
       this.callback = callback;
    }

    public static <T extends Object> CallbackFlowStrategy<T> create(Consumer<T> callback) {
        return new CallbackFlowStrategy<>(callback);
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux
                .from(super.apply(publisher))
                .doOnNext(callback);
    }
}
