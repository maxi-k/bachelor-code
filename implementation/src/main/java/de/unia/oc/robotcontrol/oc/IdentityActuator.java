/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.data.DataPayload;
import de.unia.oc.robotcontrol.flow.ActiveOutFlow;
import de.unia.oc.robotcontrol.flow.InFlows;
import de.unia.oc.robotcontrol.flow.OutFlows;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.message.Message;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IdentityActuator<M, T extends DataPayload<M> & Message<T>> implements Actuator<T, T> {

    private ActiveOutFlow<T> outFlow;
    private PassiveInFlow<T> inFlow;

    private volatile @NonNull T lastReceived;

    @SuppressWarnings("initialization")
    protected IdentityActuator(T initialValue) {
        this.lastReceived = initialValue;
        this.inFlow = InFlows.createUnbuffered(this::receiveData);
        this.outFlow = OutFlows.createOnDemand(this::getMessage);
    }

    private void receiveData(T msg) {
        this.lastReceived = msg;
        this.outFlow.get().accept(inFlow);
    }

    private T getMessage() {
        return lastReceived;
    }

    @Override
    public PassiveInFlow<T> inFlow() {
        return inFlow;
    }

    @Override
    public ActiveOutFlow<T> outFlow() {
        return outFlow;
    }
}
