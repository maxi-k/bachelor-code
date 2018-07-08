package de.unia.oc.robotcontrol.concurrent;

public interface Schedulable {

    boolean isScheduled();

    Runnable getTask();
}
