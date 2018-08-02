/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ClockType;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TransparentFlowStrategy;
import de.unia.oc.robotcontrol.message.Message;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.EmitterProcessor;

/**
 * Mock Device which echoes the bytes it received back.
 */
public class MockDeviceConnector implements Device<Message, Message> {

    private final Encoding<Message> encoding;
    private final EmitterProcessor<Message> processor;
    private boolean isTerminated = false;

    @SuppressWarnings("initialization")
    public MockDeviceConnector(Encoding<Message> encoding,
                               ScheduleProvider schedule) {
        this.encoding = encoding;
        this.processor = EmitterProcessor.create();
    }

    @Override
    public String getDeviceName() {
        return "Mock Device";
    }

    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

    @Override
    public void terminate() {
        processor.onComplete();
        isTerminated = true;
    }

    @Override
    public Processor<Message, Message> asProcessor() {
        return processor;
    }

    @Override
    public Publisher<Message> asPublisher() {
        return processor.map(encoding::encode).map(encoding::decode);
    }

    @Override
    public FlowStrategy<Message, Message> getFlowStrategy() {
        return TransparentFlowStrategy.create();
    }

    @Override
    public ClockType getClockType() {
        return ClockType.UNCLOCKED;
    }
}
