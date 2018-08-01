/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import reactor.core.publisher.Flux;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class Scheduling {

    public static TrackingScheduleProvider interval(ScheduledExecutorService exec, long delay, TimeUnit unit) {
        return new TrackingScheduleProvider(exec, TaskMode.INTERVAL, delay, unit);
    }

    public static TrackingScheduleProvider delayed(ScheduledExecutorService exec, long delay, TimeUnit unit) {
        return new TrackingScheduleProvider(exec, TaskMode.DELAYED, delay, unit);
    }

    public static <T> Flux<T> emitOn(ScheduleProvider provider, Supplier<? extends T> messageSupplier) {
        return Flux.create((downstream) -> {
            Terminable handle = provider.submit(() -> {
                T value = null;
                try {
                    value = messageSupplier.get();
                } catch (RuntimeException e) {
                    downstream.error(e);
                }
                if (value != null) downstream.next(value);
            });
            downstream.onDispose(handle::terminate);
        });
    }
}
