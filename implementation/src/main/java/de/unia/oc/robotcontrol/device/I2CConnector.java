/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ClockState;
import de.unia.oc.robotcontrol.concurrent.TimeProvider;
import de.unia.oc.robotcontrol.flow.Flow;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.FlatteningFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.MappingFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TimedFlowStrategy;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.util.Tuple;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.sql.Time;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class I2CConnector<Input extends Message, Output extends Message>
        extends LockingDeviceConnector<Input, Output> {

    private final int MAX_MESSAGE_SIZE; // = 32;
    private final int BUS;             // = I2CBus.BUS_1;
    private final byte DEVICE_ADDRESS; // = 0x04;

    private final I2CBus i2c;
    private final I2CDevice device;

    private boolean isTerminated = false;

    private volatile int lastRead;
    private final byte[] readBuffer;

    private final ClockState<Output> clockState;

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
        clockState = ClockState.create();
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

    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

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
    public FlowStrategy<Input, Output> getFlowStrategy() {
        return FlowStrategy.concat(
                super.getFlowStrategy(),
                clockState.getFlowStrategy()
        );
    }

    @Override
    public ClockType getClockType() {
        return clockState.getClockType();
    }

    @Override
    public String getDeviceName() {
        return "I2CDevice";
    }

}
