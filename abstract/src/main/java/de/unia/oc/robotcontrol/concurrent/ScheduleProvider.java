package de.unia.oc.robotcontrol.concurrent;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Describes something that provides a schedule for Tasks to be
 * executed in, that is, run with a certain delay or in a certain interval.
 */
public interface ScheduleProvider extends Terminable.TimedTerminable {

    /**
     * Add a task that should be scheduled to the Schedule
     * @param runnable The task to schedule
     */
    Terminable submit(@NonNull Runnable runnable);

    /**
     * Add an instance of {@link Schedulable} to the Schedule
     * @param schedulable The thing to schedule
     */
    default Terminable submit(@NonNull Schedulable schedulable) {
        if (schedulable.isScheduled()) {
            Runnable task = schedulable.getTask();
            if (task == null) return Terminable.ConstantlyTerminated.create();
            return submit(task);
        }
        return Terminable.ConstantlyTerminated.create();
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
    void reschedule(@Positive int capacity, @Positive long delay, @NonNull TimeUnit unit);

    /**
     * Change the schedule (interval /delay) that the tasks
     * should run on
     *
     * @param delay the unitless interval / delay between executions of a task
     * @param unit the time unit to be used for the {@param delay}
     */
    void reschedule(@Positive long delay, @NonNull TimeUnit unit);

    /**
     * Get the currently set delay / interval (unitless)
     */
    @Positive long getDelay();

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

    /**
     * @return The executor Service the tasks for this ScheduleProvider are running on
     */
    ExecutorService getExecutor();

    /**
     * Terminate this schedule and all tasks running on it,
     * blocking until all tasks have completed or the timeout has run out.
     * Like {@link java.util.concurrent.ScheduledExecutorService#awaitTermination(long, TimeUnit)}
     *
     * Also frees the list of tasks associated with this ScheduleProvider,
     * if the termination was successful.
     *
     * @param delay
     * @param unit
     * @return {@code true} if everything terminated orderly
     *         {@code false} if the timeout ran out before
     */
    @Override
    boolean terminate(@Positive long delay, @NonNull TimeUnit unit) throws InterruptedException;

    /**
     * Terminate this schedule. Do not wait until all tasks are completed.
     * Also frees the list of tasks associated with this ScheduleProvider.
     */
    @Override
    void terminate();
}
