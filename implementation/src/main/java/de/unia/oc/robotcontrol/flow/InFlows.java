package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import io.reactivex.Observer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class InFlows {


    public static <T> PassiveInFlow<T> createUnbuffered(Consumer<T> consumer) {
        return consumer::accept;
    }

    public static <T> PassiveInFlow<T> createFromObserver(Observer<T> observer) {
        return observer::onNext;
    }

    public static <T> PassiveInFlow<T> multiplex(PassiveInFlow<T>... recipients) {
        return t -> {
            for(PassiveInFlow<T> r : recipients) {
                r.accept(t);
            }
        };
    }
    public static <T> ActiveInFlow<T> createOnDemand(Consumer<T> c) {

        return new ActiveInFlow<T>() {
            @Override
            public @Nullable Runnable getTask() {
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
            @Pure
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
