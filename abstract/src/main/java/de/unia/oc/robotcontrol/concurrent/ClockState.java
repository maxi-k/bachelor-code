/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.Flowable;

public interface ClockState<T extends Object, R extends Object> extends Flowable, Clockable {

    @Override
    FlowStrategy<T, R> getFlowStrategy();

    boolean setTimer(TimeProvider provider);

}
