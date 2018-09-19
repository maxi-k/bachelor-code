/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

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

/**
 * The mediator for the whole system,
 * used to combine separately defined system parts
 * to create a functional whole.
 *
 * @param <ObserverMessage> the type of message the systems observer can receive
 * @param <WorldState> the type of report the observer outputs
 * @param <ControllerAction> the type of action the controller emits
 * @param <ObserverModel> the type of observation model observer and controller use
 *                       to communicate
 * @param <OCObserver> the type of the systems observer
 * @param <OCController> the type of the systems controller
 */
public class RobotControl<
        ObserverMessage extends Message,
        WorldState extends Object,
        ControllerAction extends Object,
        ObserverModel extends ObservationModel<WorldState>,
        OCObserver extends Observer<ObserverMessage, WorldState, ObserverModel>,
        OCController extends Controller<WorldState, ObserverModel, ControllerAction>>
        implements Terminable, Runnable {

    /**
     * Whether the overall system has been terminated.
     */
    private volatile boolean isTerminated = false;
    /**
     * Whether the overall system is currently in the
     * process of starting.
     */
    private volatile boolean isStarting = false;
    /**
     * Whether the overall system is currently running
     * (not starting or terminated)
     */
    private volatile boolean isRunning = false;

    /**
     * The systems observer.
     */
    private final OCObserver observer;
    /**
     * The systems controller.
     */
    private final OCController controller;
    /**
     * The action interpreter used to convert the commands
     * emitted by the controller into Messages that can be
     * sent to devices.
     */
    private final Function<? super ControllerAction, ? extends Message> actionInterpreter;
    /**
     * A list of the message types that the systems observer
     * wants to be subscribed to
     */
    private final Set<MessageType<? extends ObserverMessage>> observerMessageTypes;
    /**
     * Associates the different Message types with strategies used
     * before the messages are passed to the observer.
     */
    private final Map<MessageType<? extends Message>, FlowStrategy<Message, Message>> messageStrategies;
    /**
     * Associates the different devices used to communicate with external
     * resources with the {@link MessageType} instances they are interested in
     * (will receive from the controller).
     */
    private final Map<Device<? extends Message, ? extends Message>, Collection<? extends MessageType<? extends Message>>> deviceMap;

    /**
     * The execution context the devices will run on.
     */
    private final Scheduler ioScheduler;
    /**
     * The multicast used to dispatch messages between devices, observer an controller
     */
    private final EmittingMessageMulticast<Message> multicast;
    /**
     * The overall inflow coming from the devices
     */
    private final Flux<? extends Message> deviceInFlow;

    /**
     * A list of the subscriptions that the devices have from the multicast
     * used to {@link #terminate()} the system.
     */
    private @MonotonicNonNull List<Disposable> deviceSubscriptions;

    // private final Flux<? extends Message> resultingFlow;

    /**
     * Creates a new instance of {@link RobotControl} with the given parameters.
     * Should not be used from the outside directly. Use {@link #build(Observer, Controller)}
     * instead to build ob the configuration options independently one-by-one.
     *
     * @param observer the systems observer {@link #observer}
     * @param controller the systems controller {@link #controller}
     * @param actionInterpreter the systems action interpreter for controller actions {@link #actionInterpreter}
     * @param observerMessageTypes the message types the observer subscribes to {@link #observerMessageTypes}
     * @param messageStrategies the message strategy association {@link #messageStrategies}
     * @param deviceMap the association of {@code device <-> [message type]}  {@link #deviceMap}
     */
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
        // create the device in flow and try to run the devices
        // tasks on the ioScheduler
        this.deviceInFlow = Flux.merge(
                deviceMap
                        .keySet()
                        .stream()
                        .map(Device::asPublisher)
                        .collect(Collectors.toList())
        ).subscribeOn(ioScheduler);
    }

    /**
     * Actually start the system by connecting all the parts.
     * While this executes {@link #isStarting} is set to true.
     * After, {@link #isRunning} is set to true.
     */
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

        // the overall in flow from the multicast to the observer
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

        // the overall flow from multicast -> observer -> controller -> action interpreter ->
        Flux<? extends Message> controllerOutFlow = observerInFlow
                .transform(observer::apply)
                .transform(controller::apply)
                .map(actionInterpreter)
                .subscribeOn(Schedulers.newSingle("Controller Subscription"));

        // Pass Controller Messages to the Multicast
        controllerOutFlow
                .subscribe(multicast.asSubscriber());

        isRunning = true;
        isStarting = false;
    }

    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * {@inheritDoc}
     *
     * Terminates the system by terminating each {@link Device}
     * and each {@link Disposable} representing subscriptions,
     * which should end up unravelling the whole subscription chain
     * of the system.
     */
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

    /**
     * Create a new builder from the given observer and controller to configure
     * a robotcontrol instance over time and then creating it with {@link Builder#create()}
     *
     * @param observer the controller to use for the system
     * @param controller the observer to use for the system
     * @param <ObserverMessage> the type of message the systems observer can receive
     * @param <WorldState> the type of report the observer outputs
     * @param <ControllerAction> the type of action the controller emits
     * @param <ObserverModel> the type of observation model observer and controller use
     *                       to communicate
     * @param <OCObserver> the type of the systems observer
     * @param <OCController> the type of the systems controller
     * @return a new instance of {@link Builder}
     */
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

    /**
     * A builder used to configure the {@link RobotControl} Mediator with method
     * calls (method-chaining) instead of all at once in the constructor.
     *
     * Its field mirror the fields of {@link RobotControl},
     * but are modifiable and built up using the Builders methods.
     *
     * @param <OM> the type of message the systems observer can receive
     * @param <WS> the type of report the observer outputs
     * @param <CA> the type of action the controller emits
     * @param <M> the type of observation model observer and controller use
     *                       to communicate
     * @param <OCO> the type of the systems observer
     * @param <OCC> the type of the systems controller
     */
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

        /**
         * Create a new builder instance from the given observer and controller,
         * ensuring type safety throughout the rest of the method calls.
         *
         * @param observer the observer to use
         * @param controller the controller to use
         */
        private Builder(OCO observer, OCC controller) {
            this.observer = observer;
            this.controller = controller;
            this.observerMessageTypes = new HashSet<>();
            this.messageStrategies = new HashMap<>();
            this.deviceMap = new HashMap<>();
        }

        /**
         * Register the given message types in {@link #observerMessageTypes} as messages
         * the observer is interested in and will receive from the multicast.
         *
         * @param messageTypes the message types to register
         * @return this {@link Builder} for method chaining
         */
        @SafeVarargs
        public final Builder<OM, WS, CA, M, OCO, OCC> registerObserverMessages(
                MessageType<? extends OM>... messageTypes
        ) {
            this.observerMessageTypes.addAll(Arrays.asList(messageTypes));
            return this;
        }

        /**
         * Register the given message types in {@link #observerMessageTypes} as messages
         * the observer is interested in and will receive from the multicast.
         *
         * @param messageTypes the message types to register
         * @return this {@link Builder} for method chaining
         */
        public final Builder<OM, WS, CA, M, OCO, OCC> registerObserverMessages(
                Collection<? extends MessageType<? extends OM>> messageTypes
        ) {
            this.observerMessageTypes.addAll(messageTypes);
            return this;
        }

        /**
         * Set the action interpreter which sits in between controller and multicast
         * and converts the controllers action into messages.
         *
         * @param interpreter the function to use as action interpreter
         * @return this {@link Builder} for method chaining
         */
        public final Builder<OM, WS, CA, M, OCO, OCC> withActionInterpreter(
                Function<? super CA, ? extends Message> interpreter
        ) {
            this.actionInterpreter = interpreter;
            return this;
        }

        /**
         * Registers the given {@link FlowStrategy} to be used on the given {@link MessageType}
         * after being emitted from the multicast and before being passed to the observer.
         *
         * @param messageType the message type to register the flow strategy for
         * @param flowStrategy the flow strategy to use
         * @param <T> the type of the messages the message type is for
         *
         * @return this {@link Builder} for method chaining
         */
        public final <T extends Message> Builder<OM, WS, CA, M, OCO, OCC> withMessageStrategy(
                MessageType<T> messageType, FlowStrategy<Message, Message> flowStrategy
        ) {
            this.messageStrategies.put(messageType, flowStrategy);
            return this;
        }

        /**
         * Registers a new device together with the {@link MessageType}s the given
         * device is interested in to receive from the controller.
         *
         * @param device the device to register
         * @param messageTypes the message types to register
         * @param <I> the type of message this device receives as input
         * @param <O> the type of message this device sends as output
         * @return this {@link Builder} for method chaining
         */
        @SafeVarargs
        public final <I extends Message, O extends Message> Builder<OM, WS, CA, M, OCO, OCC> withDevice(
                Device<I, O> device, MessageType<? extends I>... messageTypes
        ) {
            this.deviceMap.put(device, Arrays.asList(messageTypes));
            return this;
        }

         /**
         * Registers a new device together with the {@link MessageType}s the given
         * device is interested in to receive from the controller.
         *
         * @param device the device to register
         * @param messageTypes the message types to register
         * @param <I> the type of message this device receives as input
         * @param <O> the type of message this device sends as output
         * @return this {@link Builder} for method chaining
         */
        public final <I extends Message, O extends Message> Builder<OM, WS, CA, M, OCO, OCC> withDevice(
                Device<I, O> device, Collection<? extends MessageType<I>> messageTypes
        ) {
            this.deviceMap.put(device, messageTypes);
            return this;
        }

        /**
         * Reifies this builder into an instance of {@link RobotControl} with the parameters
         * that have been given to it using the other methods.
         *
         * @return a new instance of {@link RobotControl}
         * @throws IllegalStateException if not all required parameters have been passed
         * to build a working system
         */
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
