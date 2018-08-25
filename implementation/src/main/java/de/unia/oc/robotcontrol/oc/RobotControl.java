/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.concurrent.Clock;
import de.unia.oc.robotcontrol.concurrent.Terminable;
import de.unia.oc.robotcontrol.concurrent.TimeProvider;
import de.unia.oc.robotcontrol.device.Device;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TransparentFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TypeFilterFlowStrategy;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RobotControl<
        ObserverMessage extends Message,
        WorldState extends Object,
        ControllerAction extends Object,
        ObserverModel extends ObservationModel<WorldState>,
        OCObserver extends Observer<ObserverMessage, WorldState, ObserverModel>,
        OCController extends Controller<WorldState, ObserverModel, ControllerAction>>
        implements Terminable, Runnable {

    private volatile boolean isTerminated = false;
    private volatile boolean isStarting = false;
    private volatile boolean isRunning = false;

    private final OCObserver observer;
    private final OCController controller;
    private final Function<? super ControllerAction, ? extends Message> actionInterpreter;
    private final Set<MessageType<? extends ObserverMessage>> observerMessageTypes;
    private final Map<MessageType<? extends Message>, FlowStrategy<Message, Message>> messageStrategies;
    private final Map<Device<? extends Message, ? extends Message>, Collection<? extends MessageType<? extends Message>>> deviceMap;

    private final Scheduler ioScheduler;
    private final EmittingMessageMulticast<Message> multicast;
    private final Flux<? extends Message> deviceInFlow;

    private @MonotonicNonNull List<Disposable> deviceSubscriptions;

    // private final Flux<? extends Message> resultingFlow;

    RobotControl(OCObserver observer,
                 OCController controller,
                 Function<? super ControllerAction, ? extends Message> actionInterpreter,
                 Set<MessageType<? extends ObserverMessage>> observerMessageTypes,
                 HashMap<MessageType<? extends Message>, FlowStrategy<Message, Message>> messageStrategies,
                 HashMap<Device<? extends Message, ? extends Message>, Collection<? extends MessageType<? extends Message>>> deviceMap) {

        this.observer = observer;
        this.controller = controller;
        this.actionInterpreter = actionInterpreter;

        this.observer.setObservationModel(controller.getObservationModel());
        this.controller.setObserver(observer);


        this.observerMessageTypes = Collections.unmodifiableSet(observerMessageTypes);
        this.messageStrategies = Collections.unmodifiableMap(messageStrategies);
        deviceMap.replaceAll((device, mts) -> Collections.unmodifiableCollection(mts));
        this.deviceMap = Collections.unmodifiableMap(deviceMap);

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
        isStarting = true;
        // Subscribe each device to the message types as defined
        // by the device map
        TimeProvider observerClock = this.observer.getTimeProvider();
        deviceMap.forEach((d, mt) -> d.getClockType().runOn(observerClock));

        deviceSubscriptions = deviceMap
                .entrySet()
                .stream()
                .map((entry) -> Flux.merge(
                        entry.getValue()
                                .stream()
                                .map(MessageType::asSimpleType)
                                .map((mt) ->
                                        this.messageStrategies
                                                .getOrDefault(mt, TransparentFlowStrategy.create())
                                                .apply(multicast.subscribeTo(mt)))
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

        Flux<ObserverMessage> observerInFlow = Flux.merge(
                this.observerMessageTypes
                        .stream()
                        .map(mt ->
                                this.messageStrategies
                                        .getOrDefault(mt, TransparentFlowStrategy.create())
                                        .andThen(TypeFilterFlowStrategy.create(observer.getAcceptedClass()))
                                        .apply(multicast.subscribeTo(mt))
                        ).collect(Collectors.toList())
        );

        Flux<? extends Message> controllerOutFlow = observerInFlow
                .transform(observer::apply)
                .transform(controller::apply)
                .map(actionInterpreter)
                .subscribeOn(Schedulers.newSingle("Controller Subscription"));

        // Pass Controller Messages to the Multicast
        controllerOutFlow
                .subscribe(multicast.asSubscriber());

       // this.resultingFlow.subscribeOn(Schedulers.newSingle("Robot Control"));

        isRunning = true;
        isStarting = false;
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
        isRunning = false;
    }

    public static <
            ObserverMessage extends Message,
            WorldState extends Object,
            ControllerAction extends Object,
            ObserverModel extends ObservationModel<WorldState>,
            OCObserver extends Observer<ObserverMessage, WorldState, ObserverModel>,
            OCController extends Controller<WorldState, ObserverModel, ControllerAction>
            >
    Builder<ObserverMessage, WorldState, ControllerAction, ObserverModel, OCObserver, OCController>
    build(OCObserver observer, OCController controller) {
        return new Builder<>(observer, controller);
    }

    public static final class Builder<
            OM extends Message,
            WS extends Object,
            CA extends Object,
            M extends ObservationModel<WS>,
            OCO extends Observer<OM, WS, M>,
            OCC extends Controller<WS, M, CA>> {

        private final OCO observer;
        private final OCC controller;
        private @MonotonicNonNull Function<? super CA, ? extends Message> actionInterpreter;
        private Set<MessageType<? extends OM>> observerMessageTypes;
        private HashMap<MessageType<? extends Message>, FlowStrategy<Message, Message>> messageStrategies;
        private HashMap<Device<? extends Message, ? extends Message>, Collection<? extends MessageType<? extends Message>>> deviceMap;

        private Builder(OCO observer, OCC controller) {
            this.observer = observer;
            this.controller = controller;
            this.observerMessageTypes = new HashSet<>();
            this.messageStrategies = new HashMap<>();
            this.deviceMap = new HashMap<>();
        }

        @SafeVarargs
        public final Builder<OM, WS, CA, M, OCO, OCC> registerObserverMessages(
                MessageType<? extends OM>... messageTypes
        ) {
            this.observerMessageTypes.addAll(Arrays.asList(messageTypes));
            return this;
        }

        public final Builder<OM, WS, CA, M, OCO, OCC> registerObserverMessages(
                Collection<? extends MessageType<? extends OM>> messageTypes
        ) {
            this.observerMessageTypes.addAll(messageTypes);
            return this;
        }

        public final Builder<OM, WS, CA, M, OCO, OCC> withActionInterpreter(
                Function<? super CA, ? extends Message> interpreter
        ) {
            this.actionInterpreter = interpreter;
            return this;
        }

        public final <T extends Message> Builder<OM, WS, CA, M, OCO, OCC> withMessageStrategy(
                MessageType<T> messageType, FlowStrategy<Message, Message> flowStrategy
        ) {
            this.messageStrategies.put(messageType, flowStrategy);
            return this;
        }

        @SafeVarargs
        public final <I extends Message, O extends Message> Builder<OM, WS, CA, M, OCO, OCC> withDevice(
                Device<I, O> device, MessageType<? extends I>... messageTypes
        ) {
            this.deviceMap.put(device, Arrays.asList(messageTypes));
            return this;
        }

        public final <I extends Message, O extends Message> Builder<OM, WS, CA, M, OCO, OCC> withDevice(
                Device<I, O> device, Collection<? extends MessageType<I>> messageTypes
        ) {
            this.deviceMap.put(device, messageTypes);
            return this;
        }

        public final RobotControl<OM, WS, CA, M, OCO, OCC> create() throws IllegalStateException {
            if (this.actionInterpreter == null) {
                throw new IllegalStateException("Robot Control Builder was not fully initialized!");
            }
            return new RobotControl<OM, WS, CA, M, OCO, OCC>(
                    observer,
                    controller,
                    actionInterpreter,
                    observerMessageTypes,
                    messageStrategies,
                    deviceMap
            );
        }
    }
}
