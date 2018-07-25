/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.message.Message;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Mock Device which echoes the bytes it received back.
 */
public class MockQueuedDeviceConnector extends QueuedDeviceConnector {

    private byte @MonotonicNonNull [] mockMessage;

    public MockQueuedDeviceConnector(Encoding<Message> encoding,
                                     ScheduleProvider schedule,
                                     PassiveInFlow<Message> next,
                                     Supplier<Message> updateMessageSupplier) {
        super(encoding, schedule, next, updateMessageSupplier);
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

}
