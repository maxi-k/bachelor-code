/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

public interface PublisherTransformer<T, R> extends Function<Publisher<T>, Publisher<R>> {

    static <T, R> Publisher<R> mapPublisher(
            Publisher<T> publisher,
            Function<T, R> fn
    ) {
        return liftPublisher(fn).apply(publisher);
    }

    static <T, R> PublisherTransformer<T, R> liftPublisher(Function<? super T, ? extends R> fn) {
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
    static <T1, R1, T2, R2> Processor<T2, R2> mapProcessor(
            Processor<T1, R1> processor,
            Function<T2, T1> inputFn,
            Function<R1, R2> outputFn
    ) {
        return liftProcessor(inputFn, outputFn).apply(processor);
    }

    static <T1, R1, T2, R2> Function<Processor<T1, R1>, Processor<T2, R2>> liftProcessor(
            Function<? super T2, ? extends T1> inputFn,
            Function<? super R1, ? extends R2> outputFn
    ) {
        return (Processor<T1, R1> actual) -> new Processor<T2, R2>() {

            @Override
            public void onSubscribe(Subscription s) {
                actual.onSubscribe(s);
            }

            @Override
            public void onNext(T2 t2) {
                actual.onNext(inputFn.apply(t2));
            }

            @Override
            public void onError(Throwable t) {
                actual.onError(t);
            }

            @Override
            public void onComplete() {
                actual.onComplete();
            }

            @Override
            public void subscribe(Subscriber<? super R2> actualS) {
                actual.subscribe(new Subscriber<R1>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        actualS.onSubscribe(s);
                    }

                    @Override
                    public void onNext(R1 r1) {
                        actualS.onNext(outputFn.apply(r1));
                    }

                    @Override
                    public void onError(Throwable t) {
                        actualS.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        actualS.onComplete();
                    }
                });
            }
        };
    }
}
