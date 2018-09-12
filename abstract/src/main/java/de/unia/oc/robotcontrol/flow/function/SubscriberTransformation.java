/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.function;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

/**
 * Interface for Functions that transform a {@link Subscriber} to a {@link Subscriber}.
 * Includes utility methods for creating, combining, and applying them.
 *
 * @param <I> the type of element the input subscriber can receive
 * @param <O> the type of element the resulting subscriber can receive
 */
public interface SubscriberTransformation<I, O> extends Function<Subscriber<I>, Subscriber<O>> {

    /**
     * Transform the results of the given {@link Subscriber} using the given function.
     *
     * @param subscriber the publisher to transform
     * @param fn the function used to transform the values before passing them to the
     *           given subscriber
     * @param <T> the type of value the given subscriber can receive
     * @param <R> the type of value the resulting subscriber will can receive
     * @return a new instance of {@link Subscriber}
     */
    static <T, R> Subscriber<R> mapSubscriber(Subscriber<T> subscriber,
                                              Function<R, T> fn
    ) {
        return liftSubscriber(fn).apply(subscriber);
    }

    /**
     * Return a function that, when passed a {@link Subscriber},
     * transforms the inputs of the resulting {@link Subscriber}
     * using the given function before passing them on to the
     * given {@link Subscriber}.
     *
     * @param fn the function used to transform the values before passing them to the
     *           given subscriber
     * @param <T> the type of value the given subscriber can receive
     * @param <R> the type of value the resulting subscriber will can receive
     * @return a function transforming a given instance of {@link Subscriber} to
     *          a new instance of {@link Subscriber}
     */
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

    /**
     * Create a subscriber that anonymizes the given subscription,
     * that is, it does not pass calls to the {@link Subscriber#onSubscribe(Subscription)}
     * method to the given subscriber.
     *
     * @param subscriber the subscriber to anonymize
     * @param <T> the type of value the given and resulting subscriber can receive
     * @return an instance of {@link Subscriber} that does nothing on {@link Subscriber#onSubscribe}
     */
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

    /**
     * Returns a subscriber that requests an infinite amount of values from upstream
     * when {@link Subscriber#onSubscribe(Subscription)} is called, essentially creating
     * an unbounded subscription.
     * @param subscriber the instance of {@link Subscriber} to transform
     * @param <T> the type of values that the given / resulting subscriber will
     *           be able to receive
     * @return a new instance of {@link Subscriber} that will request an unbounded
     * amount of items on {@link Subscriber#onSubscribe(Subscription)}
     */
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
