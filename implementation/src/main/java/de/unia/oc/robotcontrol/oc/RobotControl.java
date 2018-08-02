/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.concurrent.Terminable;
import de.unia.oc.robotcontrol.device.Device;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.message.EmittingMessageMulticast;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.message.MessageType;
import de.unia.oc.robotcontrol.util.Logger;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RobotControl<
        ObserverMessage extends Message,
        WoldState extends Object,
        ControllerAction extends Object,
        ObserverModel extends ObservationModel<WoldState>,
        OCObserver extends Observer<ObserverMessage, WoldState, ObserverModel>,
        OCController extends Controller<WoldState, ObserverModel, ControllerAction>>
        implements Terminable, Runnable {

    private volatile boolean isTerminated = false;

    private final OCObserver observer;
    private final OCController controller;
    private final HashMap<MessageType<? extends Message>, FlowStrategy> messageStrategies;
    private final HashMap<Device<? extends Message, ? extends Message>, List<MessageType<? extends Message>>> deviceMap;

    private final Scheduler ioScheduler;
    private final EmittingMessageMulticast<Message> multicast;
    private final Flux<? extends Message> deviceInFlow;

    private @MonotonicNonNull List<Disposable> deviceSubscriptions;

    // private final Flux<? extends Message> resultingFlow;

    RobotControl(OCObserver observer,
                 OCController controller,
                 HashMap<MessageType<? extends Message>, FlowStrategy> messageStrategies,
                 HashMap<Device<? extends Message, ? extends Message>, List<MessageType<? extends Message>>> deviceMap) {

        this.observer = observer;
        this.controller = controller;
        this.messageStrategies = messageStrategies;
        this.deviceMap = deviceMap;

        this.ioScheduler = Schedulers.newElastic("device io scheduler");

        this.multicast = new EmittingMessageMulticast<>();
        this.deviceInFlow = Flux.merge(
                deviceMap
                        .keySet()
                        .stream()
                        .map(Device::asPublisher)
                        .collect(Collectors.toList())
        ).subscribeOn(ioScheduler);
    }

    @Override
    @EnsuresNonNull({"this.deviceSubscriptions"})
    public void run() {
        // Subscribe each device to the message types as defined
        // by the device map
        deviceSubscriptions = deviceMap
                .entrySet()
                .stream()
                .map((entry) -> Flux.merge(
                        entry.getValue()
                                .stream()
                                .map(MessageType::asSimpleType)
                                .map(multicast::subscribeTo)
                                .collect(Collectors.toList())
                        ).subscribe((msg) -> {
                            if(!entry.getKey().castOnNext(msg)) {
                                Logger.instance().debugException(
                                        new IllegalArgumentException("Message could not be cast to the recipients input: [msg, recp]: " + msg + " " + entry.getKey())
                                );
                            }
                        })
                ).collect(Collectors.toList());

        // Pass messages from devices to the multicast
        deviceInFlow.subscribe(multicast.asSubscriber());

       // this.resultingFlow.subscribeOn(Schedulers.newSingle("Robot Control"));
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
        if (deviceSubscriptions != null) {
            for (Disposable d : deviceSubscriptions) {
                d.dispose();
            }
        }
        isTerminated = true;
    }

    public static Builder build() {
        return new Builder();
    }

    public static final class Builder {

    }
}
