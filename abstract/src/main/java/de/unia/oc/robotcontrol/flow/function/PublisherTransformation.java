/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.function;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

/**
 * Interface for Functions that transform a {@link Publisher} to a {@link Publisher}.
 * Includes utility methods for creating, combining, and applying them.
 *
 * @param <T> the type of element the input publisher emits
 * @param <R> the type of element the resulting publisher emits
 */
public interface PublisherTransformation<T, R>
        extends Function<Publisher<T>, Publisher<R>> {

    /**
     * Transform the results of the given {@link Publisher} using the given function.
     *
     * @param publisher the publisher to transform
     * @param fn the function used to transform the values emitted from the publisher
     * @param <T> the type of value the given publisher emits
     * @param <R> the type of value the resulting publisher will emit
     * @return a new instance of {@link Publisher}
     */
    static <T, R> Publisher<R> mapPublisher(
            Publisher<T> publisher,
            Function<T, R> fn
    ) {
        return liftPublisher(fn).apply(publisher);
    }

    /**
     * Return a function that, when passed a {@link Publisher},
     * transforms the results of the given {@link Publisher}
     * using the given function.
     *
     * @param fn the function used to transform the values emitted from the publisher
     * @param <T> the type of value the given publisher emits
     * @param <R> the type of value the resulting publisher will emit
     * @return a function transforming a given instance of {@link Publisher} to
     *          a new instance of {@link Publisher}
     */
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
