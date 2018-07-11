package de.unia.oc.robotcontrol.concurrent;

import java.util.concurrent.TimeUnit;

public interface ScheduleProvider {

    void submit(Runnable runnable);

    default void submit(Schedulable schedulable) {
        submit(schedulable.getTask());
    }

    void reschedule(int capacity, long delay, TimeUnit unit);

    void reschedule(long delay, TimeUnit unit);

    long getDelay();

    TimeUnit getTimeUnit();

    TaskMode getTaskMode();
}
