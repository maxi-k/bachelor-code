/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import java.util.concurrent.TimeUnit;

public class Timespan {

    private final int time;
    private final TimeUnit unit;

    private Timespan(int time, TimeUnit unit) {
        this.time = time;
        this.unit = unit;
    }

    public int getTime() {
        return time;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public Timespan withTime(int time) {
        return Timespan.create(time, this.unit);
    }

    public Timespan withUnit(TimeUnit unit) {
        return Timespan.create(this.time, unit);
    }

    public static Timespan create(int time, TimeUnit unit) {
        return new Timespan(time, unit);
    }
}
