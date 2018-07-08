package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import io.reactivex.Observable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class OutFlows {

    public static <T> PassiveOutFlow<T> createUnbuffered(Supplier<T> supplier) {
        return supplier::get;
    }

    public static <T> PassiveOutFlow<T> fromObservableUnbuffered(Observable<T> o) {
        return () -> o.blockingNext().iterator().next();
    }

    public static <T> ActiveOutFlow<T> createOnDemand(Supplier<T> s) {

        Consumer<InFlow<T>> pusher = (i) -> i.accept(s.get());

        return new ActiveOutFlow<T>() {

            @Override
            public boolean isScheduled() {
                return false;
            }

            @Override
            public Runnable getTask() {
                return null;
            }

            @Override
            public Consumer<InFlow<T>> get() {
                return pusher;
            }
        };
    }

    public static <T> ActiveOutFlow<T> createScheduled(ScheduleProvider schedule,
                                                       Supplier<T> s,
                                                       PassiveInFlow<T> next) {

        ActiveOutFlow<T>  result = new ActiveOutFlow<T>() {
            @Override
            public boolean isScheduled() {
                return true;
            }

            @Override
            public Runnable getTask() {
                return () -> get().accept(next);
            }

            @Override
            public Consumer<InFlow<T>> get() {
                return (i) -> i.accept(s.get());
            }
        };

        schedule.submit(result);
        return result;
    }

}
