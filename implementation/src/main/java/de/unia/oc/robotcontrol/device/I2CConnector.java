/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.message.Message;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;

public class I2CConnector extends QueuedDeviceConnector {

    private final int MAX_MESSAGE_SIZE; // = 32;
    private final int BUS;             // = I2CBus.BUS_1;
    private final byte DEVICE_ADDRESS; // = 0x04;

    private final I2CBus i2c;
    private final I2CDevice device;

    private volatile int lastRead;
    private final byte[] readBuffer;

    public I2CConnector(
            int messageSize,
            int bus,
            byte deviceAddress,
            Encoding<Message> encoding,
            ScheduleProvider schedule,
            PassiveInFlow<Message> next,
            Supplier<Message> updateRequestMessageSupplier)
            throws IOException, IllegalArgumentException {
        super(encoding, schedule, next, updateRequestMessageSupplier);
        synchronized(this) {
            this.MAX_MESSAGE_SIZE = messageSize;
            this.DEVICE_ADDRESS = deviceAddress;
            this.BUS = bus;
            try {
                this.i2c = I2CFactory.getInstance(BUS);
            } catch (I2CFactory.UnsupportedBusNumberException e) {
                throw new IllegalArgumentException("Unsupported bus number! " + BUS, e);
            }
            this.device = i2c.getDevice(DEVICE_ADDRESS);
            this.readBuffer = new byte[MAX_MESSAGE_SIZE];
        }

        // System.out.println("Testing device!");
        // pushMessage(encoding.encode(updateRequestMessageSupplier.get()));
    }

    @Override
    protected synchronized void pushMessage(byte[] message) throws IOException {
        device.write(message);
    }

    @Override
    protected synchronized byte[] retrieveMessage() throws IOException {
        int read = device.read(readBuffer, 0, readBuffer.length);
        if (read > 0) {
            lastRead = read;
            return Arrays.copyOf(readBuffer, read);
        } else {
            throw new IOException("No bytes were read");
        }
    }
}
