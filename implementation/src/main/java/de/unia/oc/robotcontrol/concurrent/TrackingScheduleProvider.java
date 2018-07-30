/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class TrackingScheduleProvider implements ScheduleProvider {

    private @NonNull ScheduledExecutorService exec;
    private @NonNull TaskMode taskMode;
    private @Positive long delay;
    private @NonNull TimeUnit unit;

    private @NonNull List<Runnable> attachedTasks;

    private TrackingScheduleProvider(ScheduledExecutorService exec, TaskMode taskMode, @Positive long delay, TimeUnit unit, List<Runnable> tasks) {
        initialize(exec, taskMode, delay, unit, tasks);
    }

    TrackingScheduleProvider(ScheduledExecutorService exec, TaskMode taskMode, @Positive long delay, TimeUnit unit) {
        this(exec, taskMode, delay, unit, new ArrayList<>());
    }

    @EnsuresNonNull({"this.exec", "this.taskMode", "this.delay", "this.unit", "this.attachedTasks"})
    private synchronized void initialize(@UnknownInitialization TrackingScheduleProvider this,
                                         ScheduledExecutorService exec,
                                         TaskMode taskMode,
                                         @Positive long delay,
                                         TimeUnit unit,
                                         List<Runnable> tasks) {
        this.exec = exec;
        this.taskMode = taskMode;
        this.delay = delay;
        this.unit = unit;
        this.attachedTasks = tasks;
        submitInitial();
    }

    @RequiresNonNull("this.attachedTasks")
    private void submitInitial(@UnknownInitialization TrackingScheduleProvider this) {
        for (Runnable t : attachedTasks)  {
            this.submitNonTracking(t);
        }
    }

    private @Nullable ScheduledFuture<?> submitNonTracking(@UnknownInitialization TrackingScheduleProvider this, Runnable task) {
        if (this.taskMode == null || this.exec == null || this.unit == null) {
            return null;
        }
        switch(taskMode) {
            case DELAYED:
                return exec.scheduleWithFixedDelay(task, 0, delay, unit);
            case INTERVAL:
                return exec.scheduleAtFixedRate(task, 0, delay, unit);
            default:
                return null;
        }
    }

    @Override
    public synchronized Terminable submit(Runnable task) {
        this.attachedTasks.add(task);
        // Don't return the ScheduledFuture as it might
        // stop being relevant after reschedule().
        ScheduledFuture<?> handle = submitNonTracking(task);
        if (handle == null) return Terminable.ConstantlyTerminated.create();
        TrackingScheduleProvider self = this;
        return Terminable.create(
                () -> handle.isDone() || handle.isCancelled(),
                () -> {
                    self.attachedTasks.remove(task);
                    handle.cancel(false);
                });
    }

    @Override
    public synchronized void reschedule(@Positive int size, @Positive long newDelay, TimeUnit newUnit) {
        int poolSize = size <= 0 ? this.attachedTasks.size() : size;
        ScheduledExecutorService newService = Executors.newScheduledThreadPool(poolSize);
        exec.shutdown();

        initialize(newService, taskMode, newDelay, newUnit, attachedTasks);
    }

    @Override
    public synchronized void reschedule(@Positive long delay, TimeUnit unit) {
        reschedule(-1, delay, unit);
    }

    @Override
    public @Positive long getDelay() {
        return delay;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return unit;
    }

    @Override
    public TaskMode getTaskMode() {
        return taskMode;
    }

    @Override
    public ScheduledExecutorService getExecutor() {
        return exec;
    }

    @Override
    public boolean isTerminated() {
        return this.exec.isTerminated();
    }

    @Override
    public boolean terminate(long delay, TimeUnit unit) throws InterruptedException {
        boolean orderly =  this.exec.awaitTermination(delay, unit);
        if (orderly) {
           this.attachedTasks.clear();
        }
        return orderly;
    }

    @Override
    public void terminate() {
        this.exec.shutdown();
        this.attachedTasks.clear();
    }

}
