/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.message.Message;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * An implementation of {@link Device} using the I2C Protocol
 * to communicate with connected devices. Assumes execution on
 * a raspberry pi, as this uses the Pi4J library.
 *
 * Extends {@link LockingDeviceConnector} to ensure there are
 * no attempts to use the communications channel multiple times
 * concurrently.
 *
 * @param <Input> the type of input message this can receive.
 * @param <Output> the type of output message this will send.
 */
public class I2CConnector<Input extends Message, Output extends Message>
        extends LockingDeviceConnector<Input, Output> {

    /**
     * The maximum message size as number of bytes sent over the wire
     * at once.
     */
    private final int MAX_MESSAGE_SIZE;
    /**
     * The bus id used to communicate
     */
    private final int BUS;
    /**
     * The address of the device this is connected to.
     */
    private final byte DEVICE_ADDRESS;

    /**
     * The Pi4J i2c bus instance used for communication
     */
    private final I2CBus i2c;
    /**
     * The Pi4J i2c device instance used for communication
     */
    private final I2CDevice device;

    /**
     * Whether this has been terminated using {@link #terminate()}.
     */
    private boolean isTerminated = false;

    /**
     * The number of bytes last read from the i2c device
     */
    private volatile int lastRead;
    /**
     * The bytes last read by the i2c device
     */
    private final byte[] readBuffer;

    /**
     * Create a new instance of {@link I2CConnector} with the given
     * maximum message size, bus id and device address.
     * Uses the given encodings and update-request-message supplier
     * to satisfy the needs of the super-constructor
     * {@link LockingDeviceConnector#LockingDeviceConnector(Encoding, Encoding, Supplier)}
     *
     * @param messageSize the maximum size of the messages sent as a
     *                    number of bytes
     * @param bus the id of the i2c bus used for communication
     * @param deviceAddress the address of the device used for communication
     * @param inputEncoding the encoding used to encode the input messages received from the system
     * @param outputEncoding the encoding used to decode the bytes received from the device
     * @param updateRequestMessageSupplier the supplier for update-request messages
     * @throws IOException if connection to the device failed
     * @throws IllegalArgumentException if the given i2c parameters were not in range, that is,
     * not accepted by the Pi4J libraries constructors.
     */
    public I2CConnector(
            int messageSize,
            int bus,
            byte deviceAddress,
            Encoding<Input> inputEncoding,
            Encoding<Output> outputEncoding,
            Supplier<Input> updateRequestMessageSupplier)
            throws IOException, IllegalArgumentException {
        super(inputEncoding, outputEncoding, updateRequestMessageSupplier);
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

    /**
     * {@inheritDoc}
     *
     * Uses {@link #device} to send the given message using i2c.
     *
     * @param message the bytes to send to the connected device
     * @throws IOException if communication failed. This is the exception thrown by the
     * Pi4J library
     */
    @Override
    protected synchronized void pushMessage(byte[] message) throws IOException {
        device.write(message);
    }

    /**
     * {@inheritDoc}
     *
     * Uses the {@link #device} to receive bytes from the device.
     * Also sets {@link #lastRead} and {@link #readBuffer} respectively.
     * Does not return {@link #readBuffer} directly, but makes a copy
     * instead.
     *
     * @return a copy of the received byte array.
     * @throws IOException if communication failed.
     */
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

    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * {@inheritDoc}
     *
     * Closes the communication with the device using Pi4J,
     * and sets {@link #isTerminated} to true.
     */
    @Override
    public void terminate() {
        try {
            i2c.close();
            isTerminated = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDeviceName() {
        return "I2CDevice";
    }

}
