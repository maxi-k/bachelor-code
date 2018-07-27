/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.flow.old.ActiveOutFlow;
import de.unia.oc.robotcontrol.flow.old.InFlows;
import de.unia.oc.robotcontrol.flow.old.OutFlows;
import de.unia.oc.robotcontrol.flow.old.PassiveInFlow;
import de.unia.oc.robotcontrol.message.ErrorMessage;
import de.unia.oc.robotcontrol.message.Message;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

/**
 * Mock Device which echoes the bytes it received back.
 */
public class MockDeviceConnector implements Device<Message> {

    private final PassiveInFlow<Message> inFlow;
    private final ActiveOutFlow<Message> outFlow;

    private byte @MonotonicNonNull [] mockMessage;
    private final Encoding<Message> encoding;
    private final PassiveInFlow<Message> next;

    @SuppressWarnings("initialization")
    public MockDeviceConnector(Encoding<Message> encoding,
                               ScheduleProvider schedule,
                               PassiveInFlow<Message> next) {
        this.encoding = encoding;
        this.next = next;
        this.inFlow = InFlows.createUnbuffered(this::pushMessage);
        this.outFlow = OutFlows.createScheduled(schedule, this::getAnswer, next);
    }

    @Override
    public PassiveInFlow<Message> inFlow() {
        return inFlow;
    }

    @Override
    public ActiveOutFlow<Message> outFlow() {
        return outFlow;
    }

    private synchronized void pushMessage(Message m) {
        this.mockMessage = encoding.encode(m);
    }

    private Message getAnswer() {
        if (mockMessage == null) return new ErrorMessage(new NullPointerException("No Message received yet."));
        return this.encoding.decode(mockMessage.clone());
    }

}
