package de.unia.oc.robotcontrol.flow;

import java.util.function.Supplier;

public interface ActiveInFlow<T> extends InFlow<Supplier<OutFlow<T>>> {

    @Override
    default PressureType getFlowPressure() {
        return PressureType.ACTIVE;
    }

}
