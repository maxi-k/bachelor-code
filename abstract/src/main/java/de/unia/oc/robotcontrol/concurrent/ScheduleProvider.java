package de.unia.oc.robotcontrol.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Describes something that provides a schedule for Tasks to be
 * executed in, that is, run with a certain delay or in a certain interval.
 */
public interface ScheduleProvider {

    /**
     * Add a task that should be scheduled to the Schedule
     * @param runnable The task to schedule
     */
    void submit(Runnable runnable);

    /**
     * Add an instance of {@link Schedulable} to the Schedule
     * @param schedulable The thing to schedule
     */
    default void submit(Schedulable schedulable) {
        submit(schedulable.getTask());
    }

    /**
     * Change the schedule (interval / delay) that the tasks
     * should run on, as well as the capacity
     * (e.g. number of tasks, threads in ThreadPool etc.)
     * of the underlying scheduling structure
     *
     * @param capacity the measure of capacity for this {@link ScheduleProvider}
     * @param delay the unitless interval / delay between executions of a task
     * @param unit the time unit to be used for the {@param delay}
     */
    void reschedule(int capacity, long delay, TimeUnit unit);

    /**
     * Change the schedule (interval /delay) that the tasks
     * should run on
     *
     * @param delay the unitless interval / delay between executions of a task
     * @param unit the time unit to be used for the {@param delay}
     */
    void reschedule(long delay, TimeUnit unit);

    /**
     * Get the currently set delay / interval (unitless)
     */
    long getDelay();

    /**
     * Get current the {@link TimeUnit} which defines the unit
     * of the {@link ScheduleProvider#getDelay()}
     */
    TimeUnit getTimeUnit();

    /**
     * Get the task mode, that is, whether tasks are run in a fixed
     * interval or with a fixed delay between the end of one execution
     * and the start of another (of one specific task)
     */
    TaskMode getTaskMode();
}
