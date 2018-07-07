package de.unia.oc.robotcontrol.flow;

import io.reactivex.Observable;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class ActiveInFlow<T> implements InFlow<OutFlow<T>> {

    @Override
    public PressureType getFlowPressure() {
        return PressureType.ACTIVE;
    }

    public static <T> ActiveInFlow<T> createOnDemand(Consumer<T> c) {
        return new ActiveInFlow<T>() {
            @Override
            public void accept(OutFlow<T> outFlow) {
                T obj = outFlow.get();
                c.accept(obj);
            }
        };
    }

    // public static <T> ActiveInFlow<T> createTimed(long interval, TimeUnit time, Consumer<T> c) {
    //     Observable.interval()
    // }
}
