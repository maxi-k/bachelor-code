package de.unia.oc.robotcontrol.flow;

import java.util.function.Consumer;

public interface ActiveOutFlow<T> extends OutFlow<Consumer<InFlow<T>>> {

    @Override
    default PressureType getFlowPressure() {
        return PressureType.ACTIVE;
    }
}
