/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.device;

import de.unia.oc.robotcontrol.example.arduino.data.RobotDrivingCommand;
import de.unia.oc.robotcontrol.example.arduino.visualization.RobotGridObject;
import de.unia.oc.robotcontrol.example.arduino.visualization.TargetGridObject;
import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.device.QueuedDeviceConnector;
import de.unia.oc.robotcontrol.flow.old.PassiveInFlow;
import de.unia.oc.robotcontrol.example.arduino.message.ArduinoMessageTypes;
import de.unia.oc.robotcontrol.example.arduino.message.DistanceDataMessage;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.example.arduino.message.SpeedCmdMessage;
import de.unia.oc.robotcontrol.util.Tuple;
import de.unia.oc.robotcontrol.visualization.*;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Supplier;

import static de.unia.oc.robotcontrol.visualization.GridDirection.LEFT;
import static de.unia.oc.robotcontrol.visualization.GridDirection.RIGHT;

public class DiscreteSimulatedRobot extends QueuedDeviceConnector {

    private final ObjectGrid simulationEnvironment;
    private final RobotGridObject robot;

    private final VisualizingWindow window;

    public DiscreteSimulatedRobot(Encoding<Message> encoding,
                                  ScheduleProvider schedule,
                                  PassiveInFlow<Message> next,
                                  Supplier<Message> updateRequestMessageProvider,
                                  ObjectGrid simulationEnvironment) {
        super(encoding, schedule, next, updateRequestMessageProvider);
        this.simulationEnvironment = simulationEnvironment;
        this.robot = new RobotGridObject();
        this.window = new VisualizingWindow(simulationEnvironment);

        setupVisualization();
    }

    @RequiresNonNull({"this.simulationEnvironment", "this.robot", "this.window"})
    private void setupVisualization(@UnderInitialization DiscreteSimulatedRobot this) {
        simulationEnvironment.fillPercentage(0.2f, (i) -> new GridObject());
        simulationEnvironment.putRandomly(new TargetGridObject());
        simulationEnvironment.putRandomly(robot);
        SwingUtilities.invokeLater(window::setup);
    }

    @Override
    protected void pushMessage(byte[] message) throws IOException {
        Message m = this.encoding.decode(message);
        actOnMessage(m);
        this.window.update();
    }

    @Override
    protected byte[] retrieveMessage() throws IOException {
        return this.encoding.encode(getGridDistances());
    }

    private void actOnMessage(Message m) {
        if (m.getType() == ArduinoMessageTypes.SPEED_CMD) {
            SpeedCmdMessage cmd = (SpeedCmdMessage) m;
            RobotDrivingCommand command = cmd.getCommand();
            if (command == null) return;
            this.robot.updateFromCommand(command);
            Tuple<Integer, Integer> next = robot.getNextXY();
            try {
                this.simulationEnvironment.move(
                        robot.getX(),
                        robot.getY(),
                        next.first,
                        next.second);
            } catch (IllegalArgumentException e) {
                System.err.println("Could not move simulated robot: ");
                e.printStackTrace();
            }
        };
    }

    private synchronized DistanceDataMessage getGridDistances() {
        GridDirection robotRot = robot.getRotation();
        return new DistanceDataMessage(
                getDistanceToOther(robotRot),
                getDistanceToOther(robotRot.cycle()),
                getDistanceToOther(robotRot.cycleCC())
        );
    }

    private int getDistanceToOther(GridDirection dir) {
        int x = robot.getX();
        int y = robot.getY();
        if (simulationEnvironment.isOnEdge(x, y, dir)) return 0;
        GridObject other = simulationEnvironment.getNextObjectInDirection(x, y, dir);
        if (other == null) return -1;
        return (dir == LEFT || dir == RIGHT) ?
                Math.abs(x - other.getX()) :
                Math.abs(y - other.getY());
    }

}