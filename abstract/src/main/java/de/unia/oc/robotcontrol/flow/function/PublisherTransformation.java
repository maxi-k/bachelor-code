/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.function;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

public interface PublisherTransformation<T, R>
        extends Function<Publisher<T>, Publisher<R>> {

    static <T, R> Publisher<R> mapPublisher(
            Publisher<T> publisher,
            Function<T, R> fn
    ) {
        return liftPublisher(fn).apply(publisher);
    }

    static <T, R> PublisherTransformation<T, R> liftPublisher(Function<? super T, ? extends R> fn) {
        return publisher -> (Publisher<R>) new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> actual) {
                publisher.subscribe(new Subscriber<T>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        actual.onSubscribe(s);
                    }

                    @Override
                    public void onNext(T t) {
                        actual.onNext(fn.apply(t));
                    }

                    @Override
                    public void onError(Throwable t) {
                        actual.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        actual.onComplete();
                    }
                });
            }
        };
    }
}
