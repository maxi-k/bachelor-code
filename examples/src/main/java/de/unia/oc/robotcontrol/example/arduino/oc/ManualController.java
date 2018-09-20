/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.oc;

import de.unia.oc.robotcontrol.example.arduino.data.ArduinoState;
import de.unia.oc.robotcontrol.example.arduino.data.RobotDrivingCommand;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.FlowStrategyType;
import de.unia.oc.robotcontrol.oc.Controller;
import de.unia.oc.robotcontrol.oc.ObservationModel;
import de.unia.oc.robotcontrol.oc.Observer;
import de.unia.oc.robotcontrol.util.Logger;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.checkerframework.checker.signedness.qual.Constant;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;

/**
 * The controller used for manually sending messages to the arudino,
 * using the command line input.
 */
public class ManualController implements Controller<ArduinoState, ObservationModel<ArduinoState>, RobotDrivingCommand> {
    private final EmitterProcessor<RobotDrivingCommand> processor;
    private final Scheduler scheduler;
    private volatile @Nullable Observer<?, ? extends ArduinoState, ? super ObservationModel<ArduinoState>> observer;

    public ManualController() {
        this.processor = EmitterProcessor.create();
        this.scheduler = Schedulers.newSingle("Manual Controller input") ;
        scheduler.schedule(this::input);
    }

    @Override
    public ConcurrencyType getConcurrencyType() {
        return ConcurrencyType.INTERNAL;
    }

    @Override
    public ObservationModel<ArduinoState> getObservationModel() {
        return new ObservationModel<ArduinoState>() {
            @Override
            public Duration getTargetUpdateTime() {
                return Duration.ofMillis(2000);
            }
        };
    }

    @Override
    public void setObserver(Observer<?, ? extends ArduinoState, ? super ObservationModel<ArduinoState>> observer) {
        this.observer = observer;
        observer.setObservationModel(this.getObservationModel());
    }

    @Override
    public @Constant Set<? extends RobotDrivingCommand> getPossibleActions() {
        Set<RobotDrivingCommand> result = new HashSet<>(Arrays.asList(RobotDrivingCommand.values()));
        return Collections.unmodifiableSet(result);
    }

    @Override
    public FlowStrategy<ArduinoState, RobotDrivingCommand> getFlowStrategy() {
        return new FlowStrategy<ArduinoState, RobotDrivingCommand>() {
            @Override
            public FlowStrategyType getType() {
                return FlowStrategyType.IGNORE;
            }

            @Override
            public Publisher<RobotDrivingCommand> apply(Publisher<ArduinoState> arduinoStatePublisher) {
                arduinoStatePublisher.subscribe(new BaseSubscriber<ArduinoState>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
                    }

                    @Override
                    protected void hookOnNext(ArduinoState value) {
                        System.out.println("controller received: " + value);
                        super.hookOnNext(value);
                    }
                });
                return processor;
            }
        };
    }

    @RequiresNonNull("this.processor")
    private void input(@UnknownInitialization ManualController this) {
        Logger.instance().println("Press 'q' to stop, p to print the last received message");
        try (Scanner reader = new Scanner(System.in)) {
            while (true) {
                try {
                    Logger.instance().println("Enter a message: ");
                    String read = reader.next();
                    char first = read.charAt(0);
                    if (first == 'q') break;
                    // if (first == 'p') {
                    //     Logger.instance().println(lastMessage != null ? lastMessage.toString() : "No last Message");
                    //     continue;
                    // }
                    // send the read command as a driving command to the arduino,
                    // with the driving direction specified by the read character
                    // with a fixed speed of 20 mmps
                    Optional<RobotDrivingCommand> dc = RobotDrivingCommand.fromIdentifier(first);
                    if (dc.isPresent()) {
                        processor.onNext(dc.get());
                    } else {
                        System.out.println("Command could not be interpreted");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Stopping...");
    }
}
