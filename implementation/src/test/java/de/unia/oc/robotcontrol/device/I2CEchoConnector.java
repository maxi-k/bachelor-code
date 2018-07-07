package de.unia.oc.robotcontrol.device;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import de.unia.oc.robotcontrol.flow.InFlow;
import de.unia.oc.robotcontrol.flow.OutFlow;
import de.unia.oc.robotcontrol.message.Message;

/**
 * Mock Device which echoes the bytes it received back.
 */
public class I2CEchoConnector implements Device<Message> {

    private final InFlow inFlow;
    private final OutFlow outFlow;

    public I2CEchoConnector() {
        this.inFlow = new
    }

    @Override
    public InFlow getInFlow() {
        return null;
    }

    @Override
    public OutFlow getOutFlow() {
        return null;
    }
}
