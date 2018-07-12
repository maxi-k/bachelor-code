/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.coding.IntegerEncoding;
import de.unia.oc.robotcontrol.coding.ListEncoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.message.Message;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class MockArduino extends QueuedDeviceConnector {

    private final Encoding<List<Integer>> mockAnswerEncoding;

    public MockArduino(Encoding<Message> encoding,
                       ScheduleProvider schedule,
                       PassiveInFlow<Message> next,
                       Supplier<Message> updateRequestMessageProvider) {
        super(encoding, schedule, next, updateRequestMessageProvider);
        this.mockAnswerEncoding = new ListEncoding<Integer>(new IntegerEncoding(encoding.getContext()), 3);
    }

    @Override
    protected void pushMessage(byte[] message) {
        // stub - message is not sent to any actual device
    }

    @Override
    protected byte[] retrieveMessage() {
        return this.mockAnswerEncoding.encode(Arrays.asList(
                (int) (Math.random() * 100),
                (int) (Math.random() * 100),
                (int) (Math.random() * 100)
        ));
    }

}
