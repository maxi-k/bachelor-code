/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.device.LockingDeviceConnector;
import de.unia.oc.robotcontrol.example.arduino.message.DistanceDataMessage;
import de.unia.oc.robotcontrol.message.Message;

import java.util.function.Supplier;

public class MockArduino extends LockingDeviceConnector<Message, Message> {

    private boolean isTerminated = false;

    public MockArduino(Encoding<Message> encoding,
                       Supplier<Message> updateRequestMessageProvider) {
        super(encoding, encoding, updateRequestMessageProvider);
    }

    @Override
    protected void pushMessage(byte[] message) {
        // stub - message is not sent to any actual device
        System.out.println("pushing message!");
    }

    @Override
    protected byte[] retrieveMessage() {
        return this.outputEncoding.encode(new DistanceDataMessage(
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
    public boolean isTerminated() {
        return isTerminated;
    }

    @Override
    public void terminate() {
        isTerminated = true;
    }
}
