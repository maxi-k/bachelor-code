/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.coding.IntegerEncoding;
import de.unia.oc.robotcontrol.coding.ListEncoding;
import de.unia.oc.robotcontrol.device.LockingDeviceConnector;
import de.unia.oc.robotcontrol.message.Message;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class MockArduino extends LockingDeviceConnector<Message, Message> {

    private final Encoding<List<Integer>> mockAnswerEncoding;
    private boolean isTerminated = false;

    public MockArduino(Encoding<Message> encoding,
                       Supplier<Message> updateRequestMessageProvider) {
        super(encoding, encoding, updateRequestMessageProvider);
        this.mockAnswerEncoding = new ListEncoding<>(new IntegerEncoding(encoding.getContext()), 3);
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

    @Override
    public String getDeviceName() {
        return "Mock Arduino";
    }

    @Override
    public ClockType getClockType() {
        return ClockType.INTERNAL;
    }

    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

    @Override
    public void terminate() {
        isTerminated = true;
    }
}
