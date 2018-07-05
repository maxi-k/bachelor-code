/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.util.Logger;
import de.unia.oc.robotcontrol.flow.ProviderStrategy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class I2CConnector extends Observable implements Device, Observer {

    private final int MAX_MESSAGE_SIZE; // = 32;
    private final int BUS;             // = I2CBus.BUS_1;
    private final byte DEVICE_ADDRESS; // = 0x04;

    private final I2CBus i2c;
    private final I2CDevice device;

    private int lastRead;
    private final byte[] readBuffer;

    private final Encoding<Message> encoding;

    public <T extends Encoding<? extends Message>> I2CConnector(int messageSize,
                        int bus,
                        byte deviceAddress,
                        Encoding<Message> encoding)
            throws IOException, I2CFactory.UnsupportedBusNumberException {
        this.MAX_MESSAGE_SIZE = messageSize;
        this.DEVICE_ADDRESS = deviceAddress;
        this.BUS = bus;
        this.encoding = encoding;
        this.i2c = I2CFactory.getInstance(I2CBus.BUS_1);
        this.device = i2c.getDevice(DEVICE_ADDRESS);
        this.readBuffer = new byte[MAX_MESSAGE_SIZE];
    }

    @Override
    public FlowStrategy getFlowType() {
        return FlowStrategy.PULL;
    }

    @Override
    public ProviderStrategy getProviderStrategy() {
        return ProviderStrategy.timed(5);
    }

    @Override
    public Observable asObservable() {
        return this;
    }

    @Override
    public Observer asObserver() {
        return this;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof Message)) {
            return;
        }
        Message msg = (Message) arg;
        try {
            sendMessage(msg);
        } catch (IOException e) {
            Logger.instance().logException(e);
        }
    }

    public <T extends Message> void sendMessage(T message) throws IOException {
        sendMessage(encoding.encode(message));
    }

    private void sendMessage(byte[] message) throws IOException {
        device.write(message);
    }

    private byte[] receiveMessage() throws IOException {
        int read = device.read(readBuffer, 0, readBuffer.length);
        if (read > 0) {
            lastRead = read;
            return Arrays.copyOf(readBuffer, read);
        } else {
            throw new IOException("No bytes were read");
        }
    }

    public Message getMessage() throws IOException {
        byte[] recv = receiveMessage();
        return this.encoding.decode(recv);
    }
}
