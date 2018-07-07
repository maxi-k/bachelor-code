package de.unia.oc.robotcontrol.flow;

import io.reactivex.Observable;
import io.reactivex.Observer;
import org.reactivestreams.Subscription;

import java.util.function.Supplier;

public abstract class PassiveOutFlow<T> implements OutFlow<T> {

    @Override
    public PressureType getFlowPressure() {
        return PressureType.PASSIVE;
    }

    public static <T> PassiveOutFlow<T> createUnbuffered(Supplier<T> supplier) {

        return new PassiveOutFlow<T>() {
            @Override
            public T get() {
                return supplier.get();
            }
        };
    }

    public static <T> PassiveOutFlow<T> fromObservableUnbuffered(Observable<T> o) {

        return new PassiveOutFlow<T>() {
            @Override
            public T get() {
                return o.blockingNext().iterator().next();
            }
        };
    }
}
