package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.flow.ActiveOutFlow;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.message.Message;

import java.util.concurrent.ScheduledFuture;

/**
 * Mock Device which echoes the bytes it received back.
 */
public class I2CEchoConnector implements Device<Message> {

    private final PassiveInFlow<Message> inFlow;
    private final ActiveOutFlow<Message> outFlow;

    private byte[] mockMessage;
    private final Encoding<Message> encoding;
    private final PassiveInFlow<Message> next;

    public I2CEchoConnector(Encoding<Message> encoding,
                            ScheduleProvider schedule,
                            PassiveInFlow<Message> next) {
        this.encoding = encoding;
        this.next = next;
        this.inFlow = PassiveInFlow.createUnbuffered(this::pushMessage);
        this.outFlow = ActiveOutFlow.createScheduled(schedule, this::getAnswer, next);
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
        return this.encoding.decode(mockMessage.clone());
    }

}
