package de.unia.oc.robotcontrol.flow;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public abstract class PassiveInFlow<T> implements InFlow<T> {

    @Override
    public PressureType getFlowPressure() {
        return PressureType.PASSIVE;
    }

    public static <T> PassiveInFlow<T> createUnbuffered(Consumer<T> consumer) {
        return new PassiveInFlow<T>() {
            @Override
            public void accept(T t) {
                consumer.accept(t);
            }
        };
    }

    public static <T> PassiveInFlow<T> createFromObserver(Observer<T> observer) {

        return new PassiveInFlow<T>() {
            @Override
            public void accept(T t) {
                observer.onNext(t);
            }
        };
    }

}
