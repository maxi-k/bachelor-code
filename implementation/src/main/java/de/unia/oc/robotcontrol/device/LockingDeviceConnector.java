/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ClockState;
import de.unia.oc.robotcontrol.concurrent.ProcessingClockState;
import de.unia.oc.robotcontrol.concurrent.TimeProvider;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.function.ProcessorTransformation;
import de.unia.oc.robotcontrol.flow.function.PublisherTransformation;
import de.unia.oc.robotcontrol.flow.function.SubscriberTransformation;
import de.unia.oc.robotcontrol.flow.strategy.BufferFlowStrategy;
import de.unia.oc.robotcontrol.message.Message;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Constant;
import org.checkerframework.dataflow.qual.Pure;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A device connector which locks the communications channel it uses
 * to guarantee that there is only one simultaneous communication happening.
 * Used to connect devices over channels which don't allow parallel usage.
 *
 * The used lock is object-internal, meaning that this does not work if there
 * are multiple connectors using the same channel.
 *
 * This device connector is also clocked, meaning it will try to
 * send update-request messages to the device for retrieving new sensory data
 * on a regular interval, as provided by the {@link #runOnClock(TimeProvider)}
 * Method. To change this behavior, overwrite {@link #runOnClock(TimeProvider)}
 * and {@link #getFlowStrategy()}.
 *
 * @param <Input> the type of message this device connector can receive
 *               (as an instance of {@link de.unia.oc.robotcontrol.flow.FlowableProcessor})
 * @param <Output> the type of message this device connector will emit
 *               (as an instance of {@link de.unia.oc.robotcontrol.flow.FlowableProcessor})
 */
public abstract class LockingDeviceConnector<Input extends Message, Output extends Message>
        implements Device<Input, Output> {

    /**
     * The encoding used for incoming messages (presumably controller commands)
     */
    protected final Encoding<Input> inputEncoding;
    /**
     * The encoding used for outgoing messages (presumably from device to observer)
     */
    protected final Encoding<Output> outputEncoding;

    /**
     * A supplier of update-request messages used to send to the
     * device on a regular interval.
     */
    protected final Supplier<Input> updateRequestMessageProvider;

    /**
     * The lock used to prevent parallel communication with the
     * connected device / over the connected communications channel.
     */
    protected final Object deviceLock;

    /**
     * The uuid of the device.
     * This is generated in the constructor.
     */
    private final UUID uuid;

    /**
     * The processor used to receive messages, process them
     * (by sending them to the device), and retrieving a response
     * from the device. The actual output as defined by {@link #asPublisher()}
     * and {@link #asProcessor()} is converted.
     */
    private final Processor<Input, Input> inputProcessor;
    /**
     * The output channel, which is defined as the {@link #getFlowStrategy()}
     * applied to the {@link #inputProcessor}.
     */
    private final Flux<Output> output;

    /**
     * A view of the {@link #inputProcessor} as an Instance of {@link Subscriber}.
     */
    private final Subscriber<Input> input;

    /**
     * The {@link ClockState} used to handle setting of the {@link TimeProvider}
     * and used to generate part of the {@link FlowStrategy} used.
     */
    protected final ClockState<Input, Input> clockState;

    /**
     * Creates a new Instance of {@link LockingDeviceConnector}, using the
     * supplied encodings to encode and decode input and output respectively.
     * The passed {@link Supplier} is used to create update-request messages for
     * when new sensory data is requested by the clock.
     *
     * @param inputEncoding the encoding used to encode input messages to bytes
     * @param outputEncoding the encoding used to decode messages from bytes received from devices
     * @param updateRequestMessageProvider the provider fore update-request messages sent to the
     *                                     device when requested by the clock
     */
    @SuppressWarnings("initialization")
    public LockingDeviceConnector(
            Encoding<Input> inputEncoding,
            Encoding<Output> outputEncoding,
            Supplier<Input> updateRequestMessageProvider) {
        this.inputEncoding = inputEncoding;
        this.outputEncoding = outputEncoding;
        this.deviceLock = new Object();

        this.updateRequestMessageProvider = updateRequestMessageProvider;
        this.clockState = createClockState();

        this.inputProcessor = createProcessor();
        this.input = wrapSubscriber(inputProcessor);
        this.output = Flux
                .from(inputProcessor)
                .publishOn(Schedulers.newSingle("deviceConnector_" + this.getDeviceName()))
                .transform(getFlowStrategy());

        this.uuid = UUID.randomUUID();
    }

    @Override
    public Publisher<Output> asPublisher() {
        return output;
    }

    @Override
    public Subscriber<Input> asSubscriber() {
        return input;
    }

    /**
     * Convert the given {@link Input} to a {@link Output} by sending it to
     * the device with the {@link #pushMessage(byte[])} method and then
     * receiving an answer with the {@link #retrieveMessage()} method.
     * The connector waits {@link #getMinRequestTimeMillis()} while this
     * transaction is happening, and locks the communications channel using the
     * {@link #deviceLock}.
     *
     * @param input the input message to send to the device
     * @return an instance of {@link Output} as decoded by {@link #outputEncoding},
     * using the bytes received from the device.
     */
    private Output sendAndReceive(Input input) {
        synchronized (deviceLock) {
            try {
                pushMessage(this.inputEncoding.encode(input));
                Thread.sleep(getMinRequestTimeMillis());
                return this.outputEncoding.decode(retrieveMessage());
            } catch (InterruptedException | IOException e) {
                System.err.println("Error while sending or retrieving message!");
                e.printStackTrace();
                throw new RuntimeException(e);
                // return new ErrorMessage(e);
            } catch (Exception e) {
                System.err.println("Unexpected error while sending or retrieving message! ");
                e.printStackTrace();
                throw e;
                // return new ErrorMessage(e);
            }
        }
    }

    /**
     * Create the {@link #inputProcessor}; used in the constructor.
     * This can be overridden in subclasses to use a different
     * processor. Creates a {@link EmitterProcessor} by default.
     *
     * @return a new {@link Processor} instance
     */
    protected Processor<Input, Input> createProcessor() {
        return EmitterProcessor.create();
    }

    /**
     * Wrap given {@link Subscriber} with a function.
     * Internally used to transform the Subscriber returned by
     * {@link #asSubscriber()}.
     *
     * By default, transforms it by allowing multiple subscriptions
     * and setting an unbounded capacity ({@link Subscription#request(long)}
     * is called with {@link Long#MAX_VALUE}),
     * so that this connector does not need to request from the upstream.
     *
     * @param processor the subscriber to wrap
     * @return an instance of {@link Subscriber}
     */
    protected Subscriber<Input> wrapSubscriber(Subscriber<Input> processor) {
        return SubscriberTransformation.unboundedSubscription(
                SubscriberTransformation.anonymizeSubscription(
                        processor
                ));
    }

    @Override
    public Processor<Input, Output> asProcessor() {
        return ProcessorTransformation
                .transformProcessor(inputProcessor, Function.identity(), getFlowStrategy());
    }

    @Override
    public FlowStrategy<Input, Output> getFlowStrategy() {
        return BufferFlowStrategy
                .<Input>create(getInputBufferSize(), BufferOverflowStrategy.DROP_OLDEST)
                .with(clockState.getFlowStrategy())
                .with(PublisherTransformation.liftPublisher(this::sendAndReceive));
    }

    /**
     * Create the instance of {@link ClockState} used in {@link #clockState} to
     * handle clocking and setting the time provider.
     *
     * @return a new instance of {@link ClockState}
     */
    protected ClockState<Input, Input> createClockState() {
        return ProcessingClockState.create(updateRequestMessageProvider,
                new BiFunction<Long, Input, Input>() {
                    @Nullable Message lastMessage = null;
                    @Override
                    public synchronized Input apply(Long time, Input input) {
                        // if the last message received from upstream is the same
                        // as the last message sent to the device, use a
                        // update request instead (because this means that there
                        // was a tick and now new command)
                        if (lastMessage == input) {
                            return updateRequestMessageProvider.get();
                        }
                        lastMessage = input;
                        return input;
                    }
                }
        );
    }

    @Override
    public final ClockType getClockType() {
        return clockState.getClockType();
    }

    @Override
    public UUID getDeviceUUID() {
        return this.uuid;
    }

    /**
     * @return the size of the buffer used to store incoming messages
     * while they can not be processed (the communications channel is locked).
     */
    @Pure
    @Constant
    protected int getInputBufferSize() {
        return 32;
    }

    /**
     * @return the time in milliseconds that {@link #sendAndReceive(Message)}
     * waits after sending a message and before trying to retrieve the
     * answer.
     */
    @Pure
    @Constant
    protected int getMinRequestTimeMillis() {
        return 10;
    }

    /**
     * Abstract method to be overridden by subclasses, which sends
     * the given bytes to the device using some specific communication
     * protocol. It is ensured that this method is called only while
     * there is no other communication, that is,
     * it is locked until {@link #retrieveMessage()} has been executed
     * completely.
     *
     * @param message the bytes to send to the connected device
     * @throws IOException if there was an error while communicating
     */
    protected abstract void pushMessage(byte[] message) throws IOException;

    /**
     * Abstract method to be overriden by subclasses, which retrieves
     * bytes from the device using some specific communication protocol.
     * It is ensured that this method is called only while there is
     * no other communication, that is, communication is locked until
     * one call to this completes.
     *
     * @return the bytes received by the connected device
     * @throws IOException if there was an error while communicating
     */
    protected abstract byte[] retrieveMessage() throws IOException;
}
