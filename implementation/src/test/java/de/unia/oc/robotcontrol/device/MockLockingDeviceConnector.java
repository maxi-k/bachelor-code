/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.message.Message;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Mock Device which echoes the bytes it received back.
 */
public class MockLockingDeviceConnector extends LockingDeviceConnector<Message, Message> {

    private boolean isTerminated = false;
    private byte @MonotonicNonNull [] mockMessage;

    public MockLockingDeviceConnector(Encoding<Message> encoding,
                                      Supplier<Message> updateMessageSupplier) {
        super(encoding, encoding, updateMessageSupplier);
    }

    @Override
    protected synchronized void pushMessage(byte[] m) {
        this.mockMessage = m ;
    }

    @Override
    protected byte[] retrieveMessage() throws IOException {
        if (mockMessage == null) throw new IOException("No Message received that mock device could answer.");
        return mockMessage.clone();
    }

    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

    @Override
    public void terminate() {
        isTerminated = true;
    }

    @Override
    public String getDeviceName() {
        return "Mock Locking Device";
    }

}
