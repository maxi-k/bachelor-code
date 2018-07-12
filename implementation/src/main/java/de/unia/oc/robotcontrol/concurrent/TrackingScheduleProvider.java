/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class TrackingScheduleProvider implements ScheduleProvider {

    private ScheduledExecutorService exec;
    private TaskMode taskMode;
    private long delay;
    private TimeUnit unit;

    private List<Runnable> attachedTasks;

    private TrackingScheduleProvider(ScheduledExecutorService exec, TaskMode taskMode, long delay, TimeUnit unit, List<Runnable> tasks) {
        initialize(exec, taskMode, delay, unit, tasks);
    }

    TrackingScheduleProvider(ScheduledExecutorService exec, TaskMode taskMode, long delay, TimeUnit unit) {
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

    @Override
    public synchronized void submit(Runnable task) {
        this.attachedTasks.add(task);
        // Don't return the ScheduledFuture as it might
        // stop being relevant after reschedule().
        submitNonTracking(task);
    }

    @Override
    public void submit(Schedulable schedulable) {
        submit(schedulable.getTask());
    }

    @Override
    public synchronized void reschedule(int size, long newDelay, TimeUnit newUnit) {
        int poolSize = size <= 0 ? this.attachedTasks.size() : size;
        ScheduledExecutorService newService = Executors.newScheduledThreadPool(poolSize);
        exec.shutdown();

        initialize(newService, taskMode, newDelay, newUnit, attachedTasks);
    }

    @Override
    public synchronized void reschedule(long delay, TimeUnit unit) {
        reschedule(-1, delay, unit);
    }

    @Override
    public long getDelay() {
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
