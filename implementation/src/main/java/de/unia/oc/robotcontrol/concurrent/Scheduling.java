/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Scheduling {

    public static TrackingScheduleProvider interval(ScheduledExecutorService exec, long delay, TimeUnit unit) {
        return new TrackingScheduleProvider(exec, TaskMode.INTERVAL, delay, unit);
    }

    public static TrackingScheduleProvider delayed(ScheduledExecutorService exec, long delay, TimeUnit unit) {
        return new TrackingScheduleProvider(exec, TaskMode.DELAYED, delay, unit);
    }
}
