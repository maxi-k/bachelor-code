/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.function;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

public interface ProcessorTransformation<I1, O1, I2, O2> extends Function<Processor<I1, O1>, Processor<I2, O2>> {

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
