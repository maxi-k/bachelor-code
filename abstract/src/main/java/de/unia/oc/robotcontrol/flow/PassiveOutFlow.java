package de.unia.oc.robotcontrol.flow;

public interface PassiveOutFlow<T> extends OutFlow<T> {

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
