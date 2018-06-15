/* 2016 */
package de.unia.oc.robotcontrol;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

public class Communicator {

    public static final int MAX_MESSAGE_SIZE = 32;
    private static final byte DEVICE_ADDRESS = 0x04;
    private static final int bus = I2CBus.BUS_1;

    private static Communicator _instance;

    private final I2CBus i2c;
    private final I2CDevice device;
    private final byte[] readBuffer;

    public Communicator() throws IOException, I2CFactory.UnsupportedBusNumberException {
        this.i2c = I2CFactory.getInstance(I2CBus.BUS_1);
        this.device = i2c.getDevice(DEVICE_ADDRESS);
        this.readBuffer = new byte[MAX_MESSAGE_SIZE];
    }

    public static Communicator instance() throws IOException, I2CFactory.UnsupportedBusNumberException {
        if (_instance == null)  {
            _instance = new Communicator();
        }
        return _instance;
    }

    public <T> void sendMessage(T message, Function<T, byte[]> encoder) throws IOException {
        sendMessage(encoder.apply(message));
    }

    public void sendMessage(byte[] message) throws IOException {
        device.write(message);
    }

    public <T> T receiveMessage(Function<byte[], T> decoder) throws IOException {
        return decoder.apply(receiveMessage());
    }

    public byte[] receiveMessage() throws IOException {
        int read = device.read(readBuffer, 0, readBuffer.length);
        if (read > 0) {
            return Arrays.copyOf(readBuffer, read);
        } else {
            throw new IOException("No bytes were read");
        }
    }

}
