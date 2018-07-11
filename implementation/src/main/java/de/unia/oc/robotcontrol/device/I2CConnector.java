/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.flow.ActiveOutFlow;
import de.unia.oc.robotcontrol.flow.InFlows;
import de.unia.oc.robotcontrol.flow.OutFlows;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.message.ErrorMessage;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.util.Logger;

import java.io.IOException;
import java.util.Arrays;

public class I2CConnector implements Device {

    private final int MAX_MESSAGE_SIZE; // = 32;
    private final int BUS;             // = I2CBus.BUS_1;
    private final byte DEVICE_ADDRESS; // = 0x04;

    private final I2CBus i2c;
    private final I2CDevice device;
    private final PassiveInFlow<Message> inFlow;
    private final ActiveOutFlow<Message> outFlow;

    private volatile int lastRead;
    private final byte[] readBuffer;

    private final Encoding<Message> encoding;

    public I2CConnector(
            int messageSize,
            int bus,
            byte deviceAddress,
            Encoding<Message> encoding,
            ScheduleProvider schedule,
            PassiveInFlow<Message> next)
            throws IOException, IllegalArgumentException {
        this.MAX_MESSAGE_SIZE = messageSize;
        this.DEVICE_ADDRESS = deviceAddress;
        this.BUS = bus;
        this.encoding = encoding;
        try {
            this.i2c = I2CFactory.getInstance(BUS);
        } catch (I2CFactory.UnsupportedBusNumberException e) {
            throw new IllegalArgumentException("Unsupported bus number! " + BUS, e);
        }
        this.device = i2c.getDevice(DEVICE_ADDRESS);
        this.readBuffer = new byte[MAX_MESSAGE_SIZE];

        this.inFlow = InFlows.createUnbuffered(this::sendMessage);
        this.outFlow = OutFlows.createScheduled(schedule, this::getMessage, next);
    }

    @Override
    public PassiveInFlow<Message> inFlow() {
        return inFlow;
    }

    @Override
    public ActiveOutFlow<Message> outFlow() {
        return outFlow;
    }

    private void sendMessage(Message message) {
        try {
            sendMessage(encoding.encode(message));
        } catch (IOException e) {
            Logger.instance().logException(e);
        }
    }

    private void sendMessage(byte[] message) throws IOException {
        device.write(message);
    }

    private byte[] receiveMessage() throws IOException {
        synchronized(readBuffer) {
            int read = device.read(readBuffer, 0, readBuffer.length);
            if (read > 0) {
                lastRead = read;
                return Arrays.copyOf(readBuffer, read);
            } else {
                throw new IOException("No bytes were read");
            }
        }
    }

    private Message getMessage() {
        try {
            byte[] recv = receiveMessage();
            return this.encoding.decode(recv);
        } catch (Exception e) {
            return new ErrorMessage<>(e);
        }
    }
}
