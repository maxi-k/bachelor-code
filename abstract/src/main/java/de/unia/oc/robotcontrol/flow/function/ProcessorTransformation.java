/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.function;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

/**
 * Interface for Functions that transform a {@link Processor} to a {@link Processor}.
 * Includes utility methods for creating, combining, and applying them.
 *
 * @param <I1> the type of element the input processor can receive
 * @param <O1> the type of element the input processor emits
 * @param <I2> the type of element the resulting processor can receive
 * @param <O2> the type of element the resulting processor emits
 */
public interface ProcessorTransformation<I1, O1, I2, O2> extends Function<Processor<I1, O1>, Processor<I2, O2>> {

    /**
     * Map the values a processor receives and emits,
     * using <code>inputFn</code> as the function that transforms
     * inputs ({@link Subscriber} side of the processor), and
     * <code>outputFn</code> to transform the outputs
     * ({@link Publisher} side of the processor)
     *
     * @param processor the {@link Processor to transform}
     * @param inputFn the function to apply to inputs before they reach the processor
     * @param outputFn the function to apply to outputs after they leave the processor
     * @param <T1> the type of values the given processor can receive
     * @param <R1> the type of values the given processor emits
     * @param <T2> the type of values the resulting processor can receive
     * @param <R2> the type of values the resulting processor will emit
     *
     * @return a new instance of {@link Processor}
     */
    static <T1, R1, T2, R2> Processor<T2, R2> mapProcessor(
            Processor<T1, R1> processor,
            Function<T2, T1> inputFn,
            Function<R1, R2> outputFn
    ) {
        return liftProcessor(inputFn, outputFn).apply(processor);
    }

    /**
     * Return a function that transformas a given {@link Processor},
     * using <code>inputFn</code> as the function that transforms
     * inputs ({@link Subscriber} side of the processor), and
     * <code>outputFn</code> to transform the outputs
     * ({@link Publisher} side of the processor)
     *
     * @param inputFn the function to apply to inputs before they reach the processor
     * @param outputFn the function to apply to outputs after they leave the processor
     * @param <T1> the type of values the given processor can receive
     * @param <R1> the type of values the given processor emits
     * @param <T2> the type of values the resulting processor can receive
     * @param <R2> the type of values the resulting processor will emit
     *
     * @return a function that will transform a given {@link Processor<T1, R1>} to
     *          a {@link Processor<T2, R2>} using the given functions
     */
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

    /**
     * Map the values a processor receives and emits,
     * using <code>subscriberFn</code> as the function that transforms
     * inputs ({@link Subscriber} side of the processor), and
     * <code>publisherFn</code> to transform the outputs
     * ({@link Publisher} side of the processor)
     *
     * @param processor the {@link Processor to transform}
     * @param subscriberFn the function to apply to inputs before they reach the processor
     *                     by transforming the {@link Subscriber} part of the passed processor
     * @param publisherFn the function to apply to outputs after they leave the processor
     *                    by transformaing the {@link Publisher} part of the passed processor
     * @param <T1> the type of values the given processor can receive
     * @param <R1> the type of values the given processor emits
     * @param <T2> the type of values the resulting processor can receive
     * @param <R2> the type of values the resulting processor will emit
     */
    static <T1, R1, T2, R2> Processor<T2, R2> transformProcessor(
            Processor<T1, R1> processor,
            Function<? super Subscriber<T1>, ? extends Subscriber<T2>> subscriberFn,
            Function<? super Publisher<R1>, ? extends Publisher<R2>> publisherFn
    ) {
        Subscriber<? super T2> sub = subscriberFn.apply(processor);
        Publisher<? extends R2> pub = publisherFn.apply(processor);

        return new Processor<T2, R2>() {
            @Override
            public void subscribe(Subscriber<? super R2> subscriber) {
                pub.subscribe(subscriber);
            }

            @Override
            public void onSubscribe(Subscription subscription) {
                sub.onSubscribe(subscription);
            }

            @Override
            public void onNext(T2 next) {
                sub.onNext(next);
            }

            @Override
            public void onError(Throwable throwable) {
                sub.onError(throwable);
            }

            @Override
            public void onComplete() {
                sub.onComplete();
            }
        };
    }
}
