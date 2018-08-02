/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ReducerFlowStrategy<T extends Object, R extends Object> implements FlowStrategy<T, R> {

    private final BiFunction<R, T, R> reducer;
    private final Supplier<R> initialValueSupplier;

    private ReducerFlowStrategy(Supplier<R> initialValueSupplier, BiFunction<R, T, R> reducer) {
        this.reducer = reducer;
        this.initialValueSupplier = initialValueSupplier;
    }

    public static <T, R> ReducerFlowStrategy<T, R> create(Supplier<R> initialValueSupplier, BiFunction<R, T, R> reducer) {
        return new ReducerFlowStrategy<>(initialValueSupplier, reducer);
    }

    @Override
    public FlowStrategyType getType() {
        return FlowStrategyType.REDUCE;
    }

    @Override
    public Publisher<R> apply(Publisher<T> publisher) {
        return Flux
                .from(publisher)
                .scanWith(initialValueSupplier, reducer)
                .onBackpressureLatest()
                .tag(PROPERTY_NAME, getType().name());
    }

   // private final class ReducingOverflowOperator extends FluxOperator<T, R> {

   //     protected ReducingOverflowOperator(Flux<? extends T> source) {
   //         super(source);
   //     }

   //     @Override
   //     public void subscribe(CoreSubscriber<? super R> actual) {
   //         source.subscribe(new ReducingSubscriber<>(actual, reducer, initialValueSupplier));
   //     }

   //     @Override
   //     public int getPrefetch() {
   //         return Integer.MAX_VALUE;
   //     }
   // }

   // private static final class ReducingSubscriber<T, R> implements CoreSubscriber<T>, Subscription {

   //     private final CoreSubscriber<? super R> actual;
   //     private final BiFunction<R, T, R> reducer;
   //     private final @Nullable Supplier<R> initialValueSupplier;

   //     volatile long numberRequested;
   //     volatile int workInProgressCount;
   //     Subscription subscription;
   //     Throwable error;
   //     volatile boolean done;
   //     volatile boolean cancelled;
   //     volatile @Nullable R reducedValue;

   //     @SuppressWarnings("rawtypes")
   //     static final AtomicLongFieldUpdater<ReducingSubscriber> NUM_REQUESTED =
   //             AtomicLongFieldUpdater.newUpdater(ReducingSubscriber.class, "numberRequested");

   //     @SuppressWarnings("rawtypes")
   //     static final AtomicIntegerFieldUpdater<ReducingSubscriber> WIP_SEMAPHORE =
   //             AtomicIntegerFieldUpdater.newUpdater(ReducingSubscriber.class, "workInProgressCount");

   //     @SuppressWarnings("rawtypes")
   //     static final AtomicReferenceFieldUpdater<ReducingSubscriber, Object> REDUCED_VALUE =
   //             AtomicReferenceFieldUpdater.newUpdater(ReducingSubscriber.class, Object.class, "reducedValue");

   //     private ReducingSubscriber(CoreSubscriber<? super R> actual, BiFunction<R, T, R> reducer, @Nullable Supplier<R> initialValueSupplier) {
   //         this.actual = actual;
   //         this.reducer = reducer;
   //         this.initialValueSupplier = initialValueSupplier;
   //     }

   //     @Override
   //     public void request(long requested) {
   //         if (!Operators.validate(requested)) return;
   //         Operators.addCap(NUM_REQUESTED, this, requested);
   //         drain();
   //     }

   //     @Override
   //     public void cancel() {
   //         if (cancelled) return;
   //         cancelled = true;
   //         subscription.cancel();

   //         if (WIP_SEMAPHORE.getAndIncrement(this) == 0) {
   //             REDUCED_VALUE.lazySet(this, null);
   //         }
   //     }

   //     @Override
   //     public void onSubscribe(Subscription s) {
   //         if (!Operators.validate(this.subscription, s)) return;
   //         this.subscription = s;
   //         actual.onSubscribe(this);
   //         s.request(Long.MAX_VALUE);
   //     }

   //     @Override
   //     public void onNext(T incoming) {
   //         REDUCED_VALUE.updateAndGet(this, (Object val) -> {
   //             if (val == null)
   //                 return reducer.apply(initialValueSupplier.get(), incoming);
   //                try {
   //                    @SuppressWarnings("unchecked")
   //                    R casted = (R) val;
   //                    return reducer.apply(casted, incoming);
   //                } catch (ClassCastException c) {
   //                    return reducer.apply(initialValueSupplier.get(), incoming);
   //                }
   //         });
   //         drain();
   //     }

   //     @Override
   //     public void onError(Throwable err) {
   //         error = err;
   //         done = true;
   //         drain();
   //     }

   //     @Override
   //     public void onComplete() {
   //         done = true;
   //         drain();
   //     }

   //     private void drain() {
   //         if (WIP_SEMAPHORE.getAndIncrement(this) != 0) {
   //             return;
   //         }
   //         final Subscriber<? super R> next = actual;

   //         int missed = 1;

   //         while (!checkTerminated(done, reducedValue == null, next)) {

   //             long req = numberRequested;
   //             long e = 0L;

   //             while (req != e) {
   //                 boolean isDone = done;

   //                 @SuppressWarnings("unchecked")
   //                 R currentValue = (R) REDUCED_VALUE.getAndSet(this, null);

   //                 boolean empty = currentValue == null;

   //                 if (checkTerminated(isDone, empty, next)) {
   //                     return;
   //                 }

   //                 if (empty) {
   //                     break;
   //                 }

   //                 next.onNext(currentValue);

   //                 e++;
   //             }

   //             if (req == e && checkTerminated(done, reducedValue == null, next)) {
   //                 return;
   //             }

   //             if (e != 0L && req != Long.MAX_VALUE) {
   //                 Operators.produced(NUM_REQUESTED, this, 1);
   //             }

   //             missed = WIP_SEMAPHORE.addAndGet(this, -missed);
   //             if (missed == 0) {
   //                 break;
   //             }
   //         } // return immediately after while loop
   //     }

   //     boolean checkTerminated(boolean isDone, boolean isEmpty, Subscriber<? super R> next) {
   //         if (cancelled) {
   //             REDUCED_VALUE.lazySet(this, null);
   //             return true;
   //         }

   //         if (isDone) {
   //             Throwable e = error;
   //             if (e != null) {
   //                 REDUCED_VALUE.lazySet(this, null);
   //                 next.onError(e);
   //                 return true;
   //             } else if (isEmpty) {
   //                 next.onComplete();
   //                 return true;
   //             }
   //         }
   //         return false;
   //     }

   // }
}
