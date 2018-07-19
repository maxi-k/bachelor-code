/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.flow.PassiveInFlow;
import de.unia.oc.robotcontrol.message.DistanceDataMessage;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.visualization.GridObject;
import de.unia.oc.robotcontrol.visualization.ObjectGrid;
import de.unia.oc.robotcontrol.visualization.VisualizedRobot;
import de.unia.oc.robotcontrol.visualization.VisualizingWindow;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Supplier;

public class SimulatedArduino extends QueuedDeviceConnector {

    private final ObjectGrid simulationEnvironment;
    private byte[] lastMessage;
    private int gridX, gridY;
    private final VisualizedRobot robot;

    private final VisualizingWindow window;

    public SimulatedArduino(Encoding<Message> encoding,
                            ScheduleProvider schedule,
                            PassiveInFlow<Message> next,
                            Supplier<Message> updateRequestMessageProvider,
                            ObjectGrid simulationEnvironment) {
        super(encoding, schedule, next, updateRequestMessageProvider);
        this.simulationEnvironment = simulationEnvironment;
        this.robot = new VisualizedRobot();
        this.window = new VisualizingWindow(simulationEnvironment);

        setupVisualization();
    }

    private void setupVisualization() {
        simulationEnvironment.fillPercentage(0.2f, (i) -> new GridObject());
        // TODO: Put food item
        simulationEnvironment.putRandomly(robot);
        SwingUtilities.invokeLater(window::setup);
    }

    @Override
    protected void pushMessage(byte[] message) throws IOException {
        Message m = this.encoding.decode(message);
        actOnMessage(m);
    }

    @Override
    protected byte[] retrieveMessage() throws IOException {
        return this.encoding.encode(new DistanceDataMessage(0, 0, 0));
    }


    private void actOnMessage(Message m) {
        System.out.println("Received command: " + m);
        // if (m.getType() == ArduinoMessageTypes.SPEED_CMD) {
        //     SpeedCmdMessage cmd = (SpeedCmdMessage) m;
        // };
    }

}
