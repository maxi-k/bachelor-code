package de.unia.oc.robotcontrol.flow;

public interface PassiveInFlow<T> extends InFlow<T> {

    @Override
    default PressureType getFlowPressure() {
        return PressureType.PASSIVE;
    }

    @Override
    default boolean isScheduled() {
        return false;
    }

    @Override
    default Runnable getTask() {
        return null;
    }

}
