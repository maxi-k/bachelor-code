/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.coding.IntegerEncoding;
import de.unia.oc.robotcontrol.coding.ListEncoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.flow.ActiveOutFlow;
import de.unia.oc.robotcontrol.flow.InFlows;
import de.unia.oc.robotcontrol.flow.OutFlows;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.message.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class MockArduino implements Device<Message> {

    private final PassiveInFlow<Message> inFlow;
    private final ActiveOutFlow<Message> outFlow;

    private final Queue<Message> messageQueue;
    private final Encoding<Message> encoding;
    private final PassiveInFlow<Message> next;
    private final Supplier<Message> updateRequestMessageProvider;

    private final Encoding<List<Integer>> mockAnswerEncoding;

    public MockArduino(Encoding<Message> encoding,
                       ScheduleProvider schedule,
                       PassiveInFlow<Message> next,
                       Supplier<Message> updateRequestMessageProvider) {
        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.encoding = encoding;
        this.next = next;
        this.updateRequestMessageProvider = updateRequestMessageProvider;

        this.inFlow = InFlows.createUnbuffered(this::queueMessage);
        this.outFlow = OutFlows.createScheduled(schedule, this::getAnswer, next);

        this.mockAnswerEncoding = new ListEncoding<Integer>(new IntegerEncoding(encoding.getContext()), 3);
    }

    @Override
    public PassiveInFlow<Message> inFlow() {
        return inFlow;
    }

    @Override
    public ActiveOutFlow<Message> outFlow() {
        return outFlow;
    }

    private void queueMessage(Message m) {
        synchronized(this.messageQueue) {
            this.messageQueue.add(m);
        }
    }

    private void pushMessage(Message m) {
        // stub - message is not sent to any actual device
    }

    private Message getAnswer() {
        synchronized (this.messageQueue) {
            Message m = this.messageQueue.isEmpty()
                    ? this.updateRequestMessageProvider.get()
                    : this.messageQueue.remove();
            pushMessage(m);
        }
        return this.encoding.decode(randomAnswer());
    }

    private byte[] randomAnswer() {
        return this.mockAnswerEncoding.encode(Arrays.asList(
                (int) (Math.random() * 100),
                (int) (Math.random() * 100),
                (int) (Math.random() * 100)
        ));
    }

}
