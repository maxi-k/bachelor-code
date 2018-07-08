package de.unia.oc.robotcontrol.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class ScheduleProvider {

    private ScheduledExecutorService exec;
    private TaskMode taskMode;
    private long delay;
    private TimeUnit unit;

    private List<Runnable> attachedTasks;

    private ScheduleProvider(ScheduledExecutorService exec, TaskMode taskMode, long delay, TimeUnit unit, List<Runnable> tasks) {
        initialize(exec, taskMode, delay, unit, tasks);
    }

    private ScheduleProvider(ScheduledExecutorService exec, TaskMode taskMode, long delay, TimeUnit unit) {
        this(exec, taskMode, delay, unit, new ArrayList<>());
    }

    private synchronized void initialize(ScheduledExecutorService exec, TaskMode taskMode, long delay, TimeUnit unit, List<Runnable> tasks) {
        this.exec = exec;
        this.taskMode = taskMode;
        this.delay = delay;
        this.unit = unit;
        this.attachedTasks = tasks;
        submitInitial();
    }

    private void submitInitial() {
        for (Runnable t : attachedTasks)  {
            this.submitNonTracking(t);
        }
    }

    private ScheduledFuture<?> submitNonTracking(Runnable task) {
        switch(taskMode) {
            case DELAYED:
                return exec.scheduleWithFixedDelay(task, 0, delay, unit);
            case INTERVAL:
                return exec.scheduleAtFixedRate(task, 0, delay, unit);
            default:
                return null;
        }
    }

    public synchronized void submit(Runnable task) {
        this.attachedTasks.add(task);
        // Don't return the ScheduledFuture as it might
        // stop being relevant after reschedule().
        submitNonTracking(task);
    }

    public void submit(Schedulable schedulable) {
        submit(schedulable.getTask());
    }

    public synchronized void reschedule(int size, long newDelay, TimeUnit newUnit) {
        int poolSize = size <= 0 ? this.attachedTasks.size() : size;
        ScheduledExecutorService newService = Executors.newScheduledThreadPool(poolSize);
        exec.shutdown();

        initialize(newService, taskMode, newDelay, newUnit, attachedTasks);
    }

    public static ScheduleProvider interval(ScheduledExecutorService exec, long delay, TimeUnit unit) {
        return new ScheduleProvider(exec, TaskMode.INTERVAL, delay, unit);
    }

    public static ScheduleProvider delayed(ScheduledExecutorService exec, long delay, TimeUnit unit) {
        return new ScheduleProvider(exec, TaskMode.DELAYED, delay, unit);
    }
}
