package de.unia.oc.robotcontrol.flow;

import io.reactivex.Observable;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ActiveInFlow<T> implements InFlow<Supplier<OutFlow<T>>> {

    @Override
    public PressureType getFlowPressure() {
        return PressureType.ACTIVE;
    }

    public static <T> ActiveInFlow<T> createOnDemand(Consumer<T> c) {

        return new ActiveInFlow<T>() {
            @Override
            public void accept(Supplier<OutFlow<T>> outFlowSupplier) {
                c.accept(outFlowSupplier.get().get());
            }
        };
    }

    // public static <T> ActiveInFlow<T> createTimed(long interval, TimeUnit time, Consumer<T> c) {
    //     Observable.interval()
    // }
}
