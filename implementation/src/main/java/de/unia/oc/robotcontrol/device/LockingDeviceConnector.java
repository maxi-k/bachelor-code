/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.PublisherTransformer;
import de.unia.oc.robotcontrol.flow.strategy.BufferFlowStrategy;
import de.unia.oc.robotcontrol.message.Message;
import org.checkerframework.checker.signedness.qual.Constant;
import org.checkerframework.dataflow.qual.Pure;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class LockingDeviceConnector<Input extends Message, Output extends Message>
        implements Device<Input, Output> {

    protected final Encoding<? super Input> inputEncoding;
    protected final Encoding<? extends Output> outputEncoding;

    protected final Supplier<? extends Input> updateRequestMessageProvider;

    protected final Object deviceLock;

    private final Processor<Input, Input> inputProcessor;
    private final Flux<Output> output;
    private final Subscriber<Input> input;

    @SuppressWarnings("initialization")
    public LockingDeviceConnector(
            Encoding<? super Input> inputEncoding,
            Encoding<? extends Output> outputEncoding,
            Supplier<? extends Input> updateRequestMessageProvider) {
        this.inputEncoding = inputEncoding;
        this.outputEncoding = outputEncoding;
        this.deviceLock = new Object();

        this.updateRequestMessageProvider = updateRequestMessageProvider;

        this.inputProcessor = createProcessor();
        this.input = inputProcessor;
        this.output = Flux
                .from(inputProcessor)
                .publishOn(Schedulers.newSingle("deviceConnector_" + this.getDeviceName()))
                .transform(getFlowStrategy());
    }

    @Override
    public Publisher<Output> asPublisher() {
        return output;
    }

    @Override
    public Subscriber<Input> asSubscriber() {
        return input;
    }

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

    protected Processor<Input, Input> createProcessor() {
        return EmitterProcessor.create();
    }

    @Override
    public Processor<Input, Output> asProcessor() {
        return PublisherTransformer
                .liftProcessor(Function.<Input>identity(), this::sendAndReceive)
                .apply(inputProcessor);
    }

    @Override
    public FlowStrategy<Input, Output> getFlowStrategy() {
        return BufferFlowStrategy
                .<Input>create(getInputBufferSize(), BufferOverflowStrategy.DROP_OLDEST)
                .chain(PublisherTransformer.liftPublisher(this::sendAndReceive));
    }

    @Pure
    @Constant
    protected int getInputBufferSize() {
        return 32;
    }

    @Pure
    @Constant
    protected int getMinRequestTimeMillis() {
        return 10;
    }

    protected abstract void pushMessage(byte[] message) throws IOException;

    protected abstract byte[] retrieveMessage() throws IOException;
}
