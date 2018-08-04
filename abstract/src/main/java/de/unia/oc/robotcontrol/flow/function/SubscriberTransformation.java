/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.function;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

public interface SubscriberTransformation<I, O> extends Function<Subscriber<I>, Subscriber<O>> {

    static <T, R> Subscriber<R> mapSubscriber(Subscriber<T> subscriber,
                                              Function<R, T> fn
    ) {
        return liftSubscriber(fn).apply(subscriber);
    }

    static <T, R> Function<Subscriber<T>, Subscriber<R>> liftSubscriber(Function<? super R, ? extends T> fn) {
        return (actual) -> new Subscriber<R>() {
            @Override
            public void onSubscribe(Subscription s) {
                actual.onSubscribe(s);
            }

            @Override
            public void onNext(R r) {
                actual.onNext(fn.apply(r));
            }

            @Override
            public void onError(Throwable t) {
                actual.onError(t);
            }

            @Override
            public void onComplete() {
                actual.onComplete();
            }
        };
    }

    static <T extends Object> Subscriber<T> anonymizeSubscription(Subscriber<T> subscriber) {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                // Don't do anything
            }

            @Override
            public void onNext(T t) {
                subscriber.onNext(t);
            }

            @Override
            public void onError(Throwable t) {
                subscriber.onError(t);
            }

            @Override
            public void onComplete() {
                subscriber.onComplete();
            }
        };
    }

    static <T extends Object> Subscriber<T> unboundedSubscription(Subscriber<T> subscriber) {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                subscriber.onSubscribe(s);
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T t) {
                subscriber.onNext(t) ;
            }

            @Override
            public void onError(Throwable t) {
                subscriber.onError(t);
            }

            @Override
            public void onComplete() {
                subscriber.onComplete();
            }
        };
    }

}
