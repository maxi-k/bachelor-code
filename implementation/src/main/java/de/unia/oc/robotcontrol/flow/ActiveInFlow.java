package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
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
            public boolean isScheduled() {
                return false;
            }

            @Override
            public Runnable getTask() {
                return null;
            }

            @Override
            public void accept(Supplier<OutFlow<T>> outFlowSupplier) {
                c.accept(outFlowSupplier.get().get());
            }
        };
    }

    public static <T> ActiveInFlow<T> createScheduled(ScheduleProvider schedule,
                                                      Consumer<T> c,
                                                      PassiveOutFlow<T> supplier) {

        ActiveInFlow<T> result = new ActiveInFlow<T>() {
            @Override
            public boolean isScheduled() {
                return true;
            }

            @Override
            public Runnable getTask() {
                return () -> accept(() -> supplier);
            }

            @Override
            public void accept(Supplier<OutFlow<T>> outFlowSupplier) {
                c.accept(outFlowSupplier.get().get());
            }
        };
        schedule.submit(result);
        return result;
    }
}
