/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.concurrent.Terminable;
import de.unia.oc.robotcontrol.device.Device;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.PublisherProvider;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.message.MessageType;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RobotControl<
        WoldState,
        ControllerAction,
        ObserverModel extends ObservationModel<WoldState>,
        OCObserver extends Observer<WoldState, ObserverModel>,
        OCController extends Controller<WoldState, ObserverModel, ControllerAction>> implements Terminable, Runnable {

    private volatile boolean isTerminated = false;

    private final OCObserver observer;
    private final OCController controller;
    private final HashMap<MessageType<? extends Message>, FlowStrategy> messageStrategies;
    private final HashMap<Device<? extends Message, ? extends Message>, List<MessageType<? extends Message>>> deviceMap;

    private final Flux<? extends Message> deviceInFlow;
    private final Flux<Message> resultingFlow;

    RobotControl(OCObserver observer,
                 OCController controller,
                 HashMap<MessageType<? extends Message>, FlowStrategy> messageStrategies,
                 HashMap<Device<? extends Message, ? extends Message>, List<MessageType<? extends Message>>> deviceMap) {

        this.observer = observer;
        this.controller = controller;
        this.messageStrategies = messageStrategies;
        this.deviceMap = deviceMap;

        this.deviceInFlow = Flux.merge(
                deviceMap
                .keySet()
                .stream()
                .map(PublisherProvider::asPublisher)
                .collect(Collectors.toList())
        );

        deviceInFlow.switchMap()
    }

    @Override
    public void run() {
       this.resultingFlow.subscribeOn(Schedulers.newSingle("Robot Control"));
    }

    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

    @Override
    public synchronized void terminate() {
        for (Device d : deviceMap.keySet()) {
            d.terminate();
        }
        isTerminated = true;
    }

    public static Builder build() {
        return new Builder();
    }

    public static final class Builder {

    }
}
