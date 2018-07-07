package de.unia.oc.robotcontrol.flow;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ActiveOutFlow<T> implements OutFlow<Consumer<InFlow<T>>> {

    @Override
    public PressureType getFlowPressure() {
        return PressureType.ACTIVE;
    }

    public static <T> ActiveOutFlow<T> createOnDemand(Supplier<T> s) {

        Consumer<InFlow<T>> pusher = (i) -> i.accept(s.get());

        return new ActiveOutFlow<T>() {
            @Override
            public Consumer<InFlow<T>> get() {
                return pusher;
            }
        };
    }
}
