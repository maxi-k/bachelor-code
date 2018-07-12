/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.flow.ActiveOutFlow;
import de.unia.oc.robotcontrol.flow.InFlows;
import de.unia.oc.robotcontrol.flow.OutFlows;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.message.ErrorMessage;
import de.unia.oc.robotcontrol.message.Message;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public abstract class QueuedDeviceConnector implements Device<Message> {

    private final PassiveInFlow<Message> inFlow;
    private final ActiveOutFlow<Message> outFlow;

    private final Queue<Message> messageQueue;
    private final Encoding<Message> encoding;
    private final PassiveInFlow<Message> next;
    private final Supplier<Message> updateRequestMessageProvider;

    public QueuedDeviceConnector(Encoding<Message> encoding,
                       ScheduleProvider schedule,
                       PassiveInFlow<Message> next,
                       Supplier<Message> updateRequestMessageProvider) {
        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.encoding = encoding;
        this.next = next;
        this.updateRequestMessageProvider = updateRequestMessageProvider;

        this.inFlow = InFlows.createUnbuffered(this::queueMessage);
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

    private void queueMessage(Message m) {
        synchronized(this.messageQueue) {
            this.messageQueue.add(m);
        }
    }

    private Message getAnswer() {
        try {
            pushMessage(this.encoding.encode(getNext()));
            Thread.sleep(10);
            return this.encoding.decode(retrieveMessage());
        } catch (InterruptedException | IOException e) {
            System.out.println("Error while sending or retrieving message!");
            e.printStackTrace();
            return new ErrorMessage(e);
        }
    }

    private Message getNext() {
        synchronized (this.messageQueue) {
            return this.messageQueue.isEmpty()
                    ? this.updateRequestMessageProvider.get()
                    : this.messageQueue.remove();
        }
    }

    protected abstract void pushMessage(byte[] message) throws IOException;

    protected abstract byte[] retrieveMessage() throws IOException;

}
